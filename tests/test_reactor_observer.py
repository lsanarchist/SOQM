"""Execute the production reactor predicate and continuity clock without a world.

Only the native reactor data is a fixture. The Java predicate and clock are read
unchanged from production source, so these checks catch the previous empty-output
buffer failure without maintaining a second implementation of the observer.
This policy harness does not replace the optional native full-pack runtime probe.
"""
from pathlib import Path
import shutil
import subprocess
import tempfile
import unittest


ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / 'src/compat/quest-automation/src/qm/questauto/QmQuestAutomation.java'


def declaration(source, marker):
    start = source.index(marker)
    opening = source.index('{', start)
    depth = 1
    for end in range(opening + 1, len(source)):
        depth += (source[end] == '{') - (source[end] == '}')
        if depth == 0:
            return source[start:end + 1]
    raise AssertionError('Unclosed Java declaration: ' + marker)


class ReactorObserverTests(unittest.TestCase):
    @unittest.skipUnless(shutil.which('java'), 'Java 17+ source launcher required for the policy harness')
    def test_fusion_export_does_not_reset_the_production_clock(self):
        source = SOURCE.read_text(encoding='utf-8')
        predicate = declaration(source, 'public static boolean fusionGenerating(')
        clock = declaration(source, 'public static final class ReactorClock ')
        observer = declaration(source, 'private static void observeReactor(')
        self.assertIn('boolean generating = fusionGenerating(data);', observer)
        harness = '''class FusionObserverRegression {
            static final class EnergyContainer {
                long stored;
                long getEnergy() { return stored; }
            }
            static final class FusionReactorMultiblockData {
                boolean formed = true, burning = true;
                long generation = 1_000_000;
                final EnergyContainer energyContainer = new EnergyContainer();
                boolean isFormed() { return formed; }
                boolean isBurning() { return burning; }
                long getPassiveGeneration(boolean active, boolean current) {
                    check(!active && current, "Observe current passive generation");
                    return generation;
                }
            }
            static void check(boolean condition, String message) {
                if (!condition) throw new AssertionError(message);
            }
            public static void main(String[] args) {
                var reactor = new FusionReactorMultiblockData();
                var fullExport = new ReactorClock();
                check(reactor.energyContainer.getEnergy() == 0, "Native output has been fully exported");
                for (int tick = 0; tick <= 200; tick += 20) {
                    boolean completed = fullExport.sample(tick, fusionGenerating(reactor), false, true);
                    check(completed == (tick == 200), "Empty buffer still needs all 200 ticks");
                }
                reactor.energyContainer.stored = 1_000_000;
                reactor.burning = false;
                check(!fusionGenerating(reactor), "Residual stored energy is not an operating reactor");
                reactor.burning = true;
                reactor.formed = false;
                check(!fusionGenerating(reactor), "Unformed reactor does not qualify");
                reactor.formed = true;
                reactor.generation = 0;
                check(!fusionGenerating(reactor), "No production does not qualify");
                reactor.generation = 1_000_000;
                var interrupted = new ReactorClock();
                for (int tick = 0; tick <= 100; tick += 20) {
                    check(!interrupted.sample(tick, fusionGenerating(reactor), false, true), "Initial partial run");
                }
                reactor.burning = false;
                check(!interrupted.sample(120, fusionGenerating(reactor), false, true), "Stop resets continuity");
                reactor.burning = true;
                for (int tick = 140; tick <= 340; tick += 20) {
                    boolean completed = interrupted.sample(tick, fusionGenerating(reactor), false, true);
                    check(completed == (tick == 340), "Restart needs a new uninterrupted 200 ticks");
                }
                var missed = new ReactorClock();
                check(!missed.sample(0, true, false, true), "First sample");
                check(!missed.sample(200, true, false, true), "Unobserved time cannot count as running");
                System.out.println("PASS: exported energy, stopped state and 200-tick continuity");
            }
        ''' + predicate + '\n' + clock + '\n}'
        with tempfile.TemporaryDirectory(prefix='soqm-fusion-regression-') as temporary:
            path = Path(temporary) / 'FusionObserverRegression.java'
            path.write_text(harness, encoding='utf-8')
            result = subprocess.run([shutil.which('java'), '-XX:-UsePerfData', '--source', '17', str(path)],
                                    capture_output=True, text=True, timeout=30)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertIn('PASS:', result.stdout)


if __name__ == '__main__':
    unittest.main()
