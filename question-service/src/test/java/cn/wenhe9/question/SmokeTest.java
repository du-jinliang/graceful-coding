package cn.wenhe9.question;

import cn.wenhe9.question.core.DatabaseTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @description: 冒烟测试
 * @author: DuJinliang
 * @create: 2023/6/17
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(DatabaseTestConfiguration.class)
public class SmokeTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 1+1=2
     */
    @Test
    public void one_plus_one_equals_two() {
        assertEquals(2, 1 + 1);
    }

    /**
     * 端点健康测试
     */
    @Test
    public void should_return_ok_when_request_endpoint_health() throws Exception {
        mockMvc
                .perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
        ;

    }
}
