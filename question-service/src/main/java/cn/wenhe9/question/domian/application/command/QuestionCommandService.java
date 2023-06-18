package cn.wenhe9.question.domian.application.command;

import cn.wenhe9.question.domian.application.command.cmd.CreateQuestionCmd;
import cn.wenhe9.question.domian.application.result.QuestionCreateResult;

/**
 * @description: 问题命令服务接口
 * @author: DuJinliang
 * @create: 2023/6/18
 */
public interface QuestionCommandService {
    QuestionCreateResult createQuestion(CreateQuestionCmd cmd);
}
