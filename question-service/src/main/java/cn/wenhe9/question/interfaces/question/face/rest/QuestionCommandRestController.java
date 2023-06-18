package cn.wenhe9.question.interfaces.question.face.rest;

import cn.wenhe9.question.domian.application.command.QuestionCommandService;
import cn.wenhe9.question.domian.application.command.cmd.CreateQuestionCmd;
import cn.wenhe9.question.interfaces.question.face.request.CreateQuestionRequest;
import cn.wenhe9.question.interfaces.question.face.response.QuestionCreateResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 问题命令接口
 * @author: DuJinliang
 * @create: 2023/6/18
 */
@RestController
@RequestMapping("/questions")
public class QuestionCommandRestController {
    private final QuestionCommandService questionCommandService;

    public QuestionCommandRestController(QuestionCommandService questionCommandService) {
        this.questionCommandService = questionCommandService;
    }

    @PostMapping("/")
    public QuestionCreateResponse createQuestion(@RequestBody CreateQuestionRequest request) {
        var cmd = new CreateQuestionCmd(request.questionerId(), request.title(), request.detail());
        var result = questionCommandService.createQuestion(cmd);
        return new QuestionCreateResponse(result.questionId());
    }
}
