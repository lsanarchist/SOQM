package soqm.validation.performance;

import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import soqm.validation.dungeons.DungeonsPerformanceProbe;
import soqm.validation.guardian.GuardianLifecycleProbe;
import soqm.validation.questauto.QuestAutomationPerformanceProbe;
import soqm.validation.worldgen.TunnelWorldgenProbe;

/** Test-only orchestration; the Python harness owns the fresh world and shutdown. */
@Mod("soqm_performance_probe")
public final class PerformanceProbe {
    private final Map<String, Object> results = new LinkedHashMap<>();
    private final Map<String, String> failures = new LinkedHashMap<>();
    private int stage;
    private int began;
    private boolean finished;

    public PerformanceProbe() { NeoForge.EVENT_BUS.addListener(this::tick); }

    private void tick(ServerTickEvent.Post event) {
        if (finished) return;
        MinecraftServer server = event.getServer();
        if (server.getTickCount() < 20) return;
        try {
            if (stage == 0) {
                if (server.getTickCount() % 20 != 0) return;
                run("tunnel_worldgen", () -> TunnelWorldgenProbe.run(server));
                run("quest_automation", () -> QuestAutomationPerformanceProbe.run(server));
                run("dungeons", () -> DungeonsPerformanceProbe.run(server));
                run("guardians", () -> GuardianLifecycleProbe.run(server));
                began = server.getTickCount();
                stage = 1;
                write("running", false);
            } else if (stage == 1) {
                boolean dungeons = advance("dungeons", () -> DungeonsPerformanceProbe.tick(server));
                boolean guardians = advance("guardians", () -> GuardianLifecycleProbe.tick(server));
                if (dungeons && guardians) {
                    stage = 2;
                    write("awaiting_reload", false);
                } else if (server.getTickCount() - began > 600) {
                    throw new AssertionError("Lifecycle fixtures did not finish within 600 ticks");
                }
            } else if (stage == 2 && Files.exists(Path.of("soqm_probe_reload_complete"))) {
                advance("dungeons", () -> { DungeonsPerformanceProbe.afterReload(server); return true; });
                advance("guardians", () -> { GuardianLifecycleProbe.afterReload(server); return true; });
                results.put("resource_reload", true);
                finished = true;
                write("finished", failures.isEmpty());
            }
        } catch (Throwable failure) {
            finished = true;
            results.put("failure", failure.toString());
            failure.printStackTrace();
            try { write("finished", false); }
            catch (Exception outputFailure) { outputFailure.printStackTrace(); }
        }
    }

    @FunctionalInterface
    private interface Checked<T> { T get() throws Throwable; }

    private void run(String name, Checked<Map<String, Object>> fixture) {
        try { results.put(name, fixture.get()); }
        catch (Throwable failure) { recordFailure(name, failure); }
    }

    private boolean advance(String name, Checked<Boolean> fixture) {
        if (failures.containsKey(name)) return true;
        try { return fixture.get(); }
        catch (Throwable failure) { recordFailure(name, failure); return true; }
    }

    private void recordFailure(String name, Throwable failure) {
        Throwable cause = failure;
        while (cause.getCause() != null && cause.getCause() != cause) cause = cause.getCause();
        failures.put(name, cause.toString());
        results.put("failures", failures);
        failure.printStackTrace();
    }

    private void write(String phase, boolean passed) throws Exception {
        results.put("phase", phase);
        results.put("passed", passed);
        Files.writeString(Path.of("soqm_probe_result.json"), new GsonBuilder().setPrettyPrinting().create().toJson(results));
    }
}
