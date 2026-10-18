package qm.chancelifecycle.mixin;
import chanceCubes.util.Scheduler;
import chanceCubes.util.Task;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(value=Scheduler.class, remap=false)
public interface SchedulerAccess {
    @Accessor("tasks")
    static List<Task> qmTasks() { throw new AssertionError("Mixin accessor was not transformed"); }
}

