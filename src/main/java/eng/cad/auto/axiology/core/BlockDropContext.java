package eng.cad.auto.axiology.core;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayDeque;
import java.util.Deque;

public class BlockDropContext {
    public record Context(Player miner, ItemStack tool) {}

    private static final ThreadLocal<Deque<Context>> CONTEXT =
            ThreadLocal.withInitial(ArrayDeque::new);

    public static void push(Player miner, ItemStack tool) {
        CONTEXT.get().push(new Context(miner, tool));
    }

    public static Context get() {
        Deque<Context> stack = CONTEXT.get();
        return stack.peek();
    }

    public static void pop() {
        Deque<Context> stack = CONTEXT.get();

        if (!stack.isEmpty()) {
            stack.pop();
        }

        if (stack.isEmpty()) {
            CONTEXT.remove();
        }
    }

    private BlockDropContext() {}
}
