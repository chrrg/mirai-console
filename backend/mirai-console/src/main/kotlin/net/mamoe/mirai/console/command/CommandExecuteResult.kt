/*
 * Copyright 2019-2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AFFERO GENERAL PUBLIC LICENSE version 3 license that can be found via the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

@file:Suppress("unused")

package net.mamoe.mirai.console.command

import net.mamoe.mirai.console.command.CommandExecuteResult.CommandExecuteStatus
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageChain
import kotlin.contracts.contract

/**
 * 指令的执行返回
 *
 * @see CommandExecuteStatus
 */
@ConsoleExperimentalApi("Not yet implemented")
@ExperimentalCommandDescriptors
public sealed class CommandExecuteResult {
    /** 指令最终执行状态 */
    public abstract val status: CommandExecuteStatus

    /** 指令执行时发生的错误 (如果有) */
    public abstract val exception: Throwable?

    /** 尝试执行的指令 (如果匹配到) */
    public abstract val command: Command?

    /** 尝试执行的指令名 (如果匹配到) */
    public abstract val commandName: String?

    /** 基础分割后的实际参数列表, 元素类型可能为 [Message] 或 [String] */
    public abstract val args: MessageChain?

    // abstract val to allow smart casting

    /** 指令执行成功 */
    public class Success(
        /** 尝试执行的指令 */
        public override val command: Command,
        /** 尝试执行的指令名 */
        public override val commandName: String,
        /** 基础分割后的实际参数列表, 元素类型可能为 [Message] 或 [String] */
        public override val args: MessageChain
    ) : CommandExecuteResult() {
        /** 指令执行时发生的错误, 总是 `null` */
        public override val exception: Nothing? get() = null

        /** 指令最终执行状态, 总是 [CommandExecuteStatus.SUCCESSFUL] */
        public override val status: CommandExecuteStatus get() = CommandExecuteStatus.SUCCESSFUL
    }

    /** 执行执行时发生了一个非法参数错误 */
    public class IllegalArgument(
        /** 指令执行时发生的错误 */
        public override val exception: IllegalCommandArgumentException,
        /** 尝试执行的指令 */
        public override val command: Command,
        /** 尝试执行的指令名 */
        public override val commandName: String,
        /** 基础分割后的实际参数列表, 元素类型可能为 [Message] 或 [String] */
        public override val args: MessageChain
    ) : CommandExecuteResult() {
        /** 指令最终执行状态, 总是 [CommandExecuteStatus.EXECUTION_EXCEPTION] */
        public override val status: CommandExecuteStatus get() = CommandExecuteStatus.ILLEGAL_ARGUMENT
    }

    /** 指令执行过程出现了错误 */
    public class ExecutionFailed(
        /** 指令执行时发生的错误 */
        public override val exception: Throwable,
        /** 尝试执行的指令 */
        public override val command: Command,
        /** 尝试执行的指令名 */
        public override val commandName: String,
        /** 基础分割后的实际参数列表, 元素类型可能为 [Message] 或 [String] */
        public override val args: MessageChain
    ) : CommandExecuteResult() {
        /** 指令最终执行状态, 总是 [CommandExecuteStatus.EXECUTION_EXCEPTION] */
        public override val status: CommandExecuteStatus get() = CommandExecuteStatus.EXECUTION_EXCEPTION
    }

    /** 没有匹配的指令 */
    public class UnresolvedCall(
        /** 尝试执行的指令名 */
        public override val commandName: String,
    ) : CommandExecuteResult() {
        /** 指令执行时发生的错误, 总是 `null` */
        public override val exception: Nothing? get() = null

        /** 尝试执行的指令, 总是 `null` */
        public override val command: Nothing? get() = null

        /** 基础分割后的实际参数列表, 元素类型可能为 [Message] 或 [String] */
        public override val args: Nothing? get() = null

        /** 指令最终执行状态, 总是 [CommandExecuteStatus.COMMAND_NOT_FOUND] */
        public override val status: CommandExecuteStatus get() = CommandExecuteStatus.COMMAND_NOT_FOUND
    }

    /** 权限不足 */
    public class PermissionDenied(
        /** 尝试执行的指令 */
        public override val command: Command,
        /** 尝试执行的指令名 */
        public override val commandName: String
    ) : CommandExecuteResult() {
        /** 基础分割后的实际参数列表, 元素类型可能为 [Message] 或 [String] */
        public override val args: Nothing? get() = null

        /** 指令执行时发生的错误, 总是 `null` */
        public override val exception: Nothing? get() = null

        /** 指令最终执行状态, 总是 [CommandExecuteStatus.PERMISSION_DENIED] */
        public override val status: CommandExecuteStatus get() = CommandExecuteStatus.PERMISSION_DENIED
    }

    /**
     * 指令的执行状态
     */
    public enum class CommandExecuteStatus {
        /** 指令执行成功 */
        SUCCESSFUL,

        /** 指令执行过程出现了错误 */
        EXECUTION_EXCEPTION,

        /** 没有匹配的指令 */
        COMMAND_NOT_FOUND,

        /** 权限不足 */
        PERMISSION_DENIED,
        /** 非法参数 */
        ILLEGAL_ARGUMENT,
    }
}

@Suppress("RemoveRedundantQualifierName")
public typealias CommandExecuteStatus = CommandExecuteResult.CommandExecuteStatus

/**
 * 当 [this] 为 [CommandExecuteResult.Success] 时返回 `true`
 */
@JvmSynthetic
public fun CommandExecuteResult.isSuccess(): Boolean {
    contract {
        returns(true) implies (this@isSuccess is CommandExecuteResult.Success)
        returns(false) implies (this@isSuccess !is CommandExecuteResult.Success)
    }
    return this is CommandExecuteResult.Success
}

/**
 * 当 [this] 为 [CommandExecuteResult.IllegalArgument] 时返回 `true`
 */
@JvmSynthetic
public fun CommandExecuteResult.isIllegalArgument(): Boolean {
    contract {
        returns(true) implies (this@isIllegalArgument is CommandExecuteResult.IllegalArgument)
        returns(false) implies (this@isIllegalArgument !is CommandExecuteResult.IllegalArgument)
    }
    return this is CommandExecuteResult.IllegalArgument
}

/**
 * 当 [this] 为 [CommandExecuteResult.ExecutionFailed] 时返回 `true`
 */
@JvmSynthetic
public fun CommandExecuteResult.isExecutionException(): Boolean {
    contract {
        returns(true) implies (this@isExecutionException is CommandExecuteResult.ExecutionFailed)
        returns(false) implies (this@isExecutionException !is CommandExecuteResult.ExecutionFailed)
    }
    return this is CommandExecuteResult.ExecutionFailed
}

/**
 * 当 [this] 为 [CommandExecuteResult.PermissionDenied] 时返回 `true`
 */
@JvmSynthetic
public fun CommandExecuteResult.isPermissionDenied(): Boolean {
    contract {
        returns(true) implies (this@isPermissionDenied is CommandExecuteResult.PermissionDenied)
        returns(false) implies (this@isPermissionDenied !is CommandExecuteResult.PermissionDenied)
    }
    return this is CommandExecuteResult.PermissionDenied
}

/**
 * 当 [this] 为 [CommandExecuteResult.UnresolvedCall] 时返回 `true`
 */
@JvmSynthetic
public fun CommandExecuteResult.isCommandNotFound(): Boolean {
    contract {
        returns(true) implies (this@isCommandNotFound is CommandExecuteResult.UnresolvedCall)
        returns(false) implies (this@isCommandNotFound !is CommandExecuteResult.UnresolvedCall)
    }
    return this is CommandExecuteResult.UnresolvedCall
}

/**
 * 当 [this] 为 [CommandExecuteResult.ExecutionFailed], [CommandExecuteResult.IllegalArgument] 或 [CommandExecuteResult.UnresolvedCall] 时返回 `true`
 */
@JvmSynthetic
public fun CommandExecuteResult.isFailure(): Boolean {
    contract {
        returns(true) implies (this@isFailure !is CommandExecuteResult.Success)
        returns(false) implies (this@isFailure is CommandExecuteResult.Success)
    }
    return this !is CommandExecuteResult.Success
}