package cn.wenhe9.question.interfaces.question.face.rest;

import cn.wenhe9.question.domian.application.command.QuestionCommandService;
import cn.wenhe9.question.domian.application.result.QuestionCreateResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @description:
 * @author: DuJinliang
 * @create: 2023/6/18
 */
@WebMvcTest
class QuestionCommandRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestionCommandService questionCommandService;

    @Test
    void should_return_ok_when_create_question() throws Exception {
        var questionId = "1";
        given(questionCommandService.createQuestion(any())).willReturn(new QuestionCreateResult(questionId));

        var requestBody = new ClassPathResource("request/question/create-question/200-ok.json").getInputStream().readAllBytes();

        mockMvc
                .perform(
                    post("/questions/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionId").value(questionId))
        ;
    }
}
