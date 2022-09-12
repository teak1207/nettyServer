package com.devgong.nettyserver.protocol.preinstall;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PreInstallRequestTest {

    @Test
    void makeRequestTest() {
        byte[] modemPhoneNumber = new byte[16];
        modemPhoneNumber[0] = '8';
        modemPhoneNumber[1] = '2';
        modemPhoneNumber[2] = '1';
        modemPhoneNumber[3] = '0';
        modemPhoneNumber[4] = '-';
        modemPhoneNumber[5] = '2';
        modemPhoneNumber[6] = '0';
        modemPhoneNumber[7] = '8';
        modemPhoneNumber[8] = '8';
        modemPhoneNumber[9] = '-';
        modemPhoneNumber[10] = '7';
        modemPhoneNumber[11] = '5';
        modemPhoneNumber[12] = '6';
        modemPhoneNumber[13] = '5';

        byte[] debugMessage = new byte[13];
        debugMessage[0] = '0';
        debugMessage[1] = '0';
        debugMessage[2] = ' ';
        debugMessage[3] = 'N';
        debugMessage[4] = 'O';
        debugMessage[5] = 'N';
        debugMessage[6] = 'E';

        byte[] inputStream = new byte[29];
        System.arraycopy(modemPhoneNumber, 0, inputStream, 0, 16);
        System.arraycopy(debugMessage, 0, inputStream, 16, 13);

        PreInstallRequest request = new PreInstallRequest(inputStream);

        assertThat(request.serialize().length).isEqualTo(29);
    }
}
