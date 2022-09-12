package com.devgong.nettyserver.repository;

import com.devgong.nettyserver.domain.PreInstallSensorListAllModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PreInstallSensorListAllRepositoryTest {

    @Autowired
    private PreInstallSensorListAllRepository repository;

    @BeforeEach
    void init() {
        assertThat(repository).isNotNull();
    }

    @Test
    void fmlsd() {
        PreInstallSensorListAllModel model = repository.findPreInstallSensorListAllModelByMphone("8212-3282-3245");

        System.out.println(model);
        System.out.println(model.getMphone().getBytes().length);
        assertThat(model).isNotNull();
    }

}