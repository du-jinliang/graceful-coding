package cn.wenhe9.question.domian.application.command.cmd;

/**
 * @description: 创建问题命令
 * @author: DuJinliang
 * @create: 2023/6/18
 */
public record CreateQuestionCmd(
        String questionerId,
        String title,
        String detail
) {
}
