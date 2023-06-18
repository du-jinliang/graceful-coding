package cn.wenhe9.question.core;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import javax.sql.DataSource;

/**
 * @description: 数据库测试配置类
 * @author: DuJinliang
 * @create: 2023/6/18
 */
public class DatabaseTestConfiguration {
    @Bean(initMethod = "start", destroyMethod = "stop")
    public MySQLContainer<?> mySqlContainer() {
        return new MySQLContainer<>("mysql:8")
                .withEnv("MYSQL_ROOT_HOST", "%")
                .withEnv("MYSQL_ROOT_PASSWORD", "0209")
                .withUsername("root")
                .withPassword("0209")
                .waitingFor(Wait.forListeningPort());
    }

    @Bean
    @FlywayDataSource
    public DataSource dataSource(MySQLContainer<?> mySqlContainer) {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(mySqlContainer.getJdbcUrl());
        hikariConfig.setUsername(mySqlContainer.getUsername());
        hikariConfig.setPassword(mySqlContainer.getPassword());

        return new HikariDataSource(hikariConfig);
    }
}
