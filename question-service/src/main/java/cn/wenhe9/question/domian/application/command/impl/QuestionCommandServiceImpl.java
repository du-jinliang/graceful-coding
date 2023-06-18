package cn.wenhe9.question.domian.application.command.impl;

import cn.wenhe9.question.domian.application.command.QuestionCommandService;
import cn.wenhe9.question.domian.application.command.cmd.CreateQuestionCmd;
import cn.wenhe9.question.domian.application.result.QuestionCreateResult;
import cn.wenhe9.question.domian.model.Question;
import cn.wenhe9.question.domian.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @description: 问题命令服务接口实现类
 * @author: DuJinliang
 * @create: 2023/6/18
 */
@Transactional
@Service("questionCommandServiceImpl")
public class QuestionCommandServiceImpl implements QuestionCommandService {

    private final QuestionRepository questionRepository;

    public QuestionCommandServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public QuestionCreateResult createQuestion(CreateQuestionCmd cmd) {
        var question = new Question(cmd.questionerId(), cmd.title(), cmd.detail());
        questionRepository.save(question);
        return new QuestionCreateResult(question.getId());
    }
}
