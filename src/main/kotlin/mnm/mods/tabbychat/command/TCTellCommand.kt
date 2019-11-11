package mnm.mods.tabbychat.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import mnm.mods.tabbychat.TabbyChat
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands.argument
import net.minecraft.command.Commands.literal
import net.minecraft.command.arguments.ComponentArgument
import net.minecraft.command.arguments.EntityArgument

object TCTellCommand : Command<CommandSource> {

    private const val TARGETS = "targets"
    private const val CHANNEL = "channel"
    private const val MESSAGE = "message"

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher.register(literal("tctell")
                .requires { it.hasPermissionLevel(2) }
                .then(argument(TARGETS, EntityArgument.players())
                        .then(argument(CHANNEL, StringArgumentType.string())
                                .then(argument(MESSAGE, ComponentArgument.component())
                                        .executes(this)))))
    }

    @Throws(CommandSyntaxException::class)
    override fun run(context: CommandContext<CommandSource>): Int {

        val players = EntityArgument.getPlayers(context, TARGETS)
        val channel = "#" + StringArgumentType.getString(context, CHANNEL)
        val message = ComponentArgument.getComponent(context, MESSAGE)

        for (player in players) {
            TabbyChat.sendTo(player, channel, message)
        }

        return players.size
    }

}
