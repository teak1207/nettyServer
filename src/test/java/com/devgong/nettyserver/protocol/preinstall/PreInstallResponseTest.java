package com.devgong.nettyserver.protocol.preinstall;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PreInstallResponseTest {

    @Test
    void makeResponseTest() {
        byte[] recordTime1 = new byte[4];
        recordTime1[0] = 'h';
        recordTime1[1] = 'h';
        recordTime1[2] = 'm';
        recordTime1[3] = 'm';

        byte[] recordTime2 = new byte[4];
        recordTime2[0] = 'h';
        recordTime2[1] = 'h';
        recordTime2[2] = 'm';
        recordTime2[3] = 'm';

        byte[] recordTime3 = new byte[4];
        recordTime3[0] = 'h';
        recordTime3[1] = 'h';
        recordTime3[2] = 'm';
        recordTime3[3] = 'm';

        byte[] fmRadio = new byte[4];
        fmRadio[0] = '0';
        fmRadio[1] = '9';
        fmRadio[2] = '5';
        fmRadio[3] = '9';

        byte[] sid = new byte[16];
        sid[0] = 's';
        sid[1] = 'i';
        sid[2] = 'd';

        byte[] pname = new byte[16];
        pname[0] = 'p';
        pname[1] = 'n';
        pname[2] = 'a';
        pname[3] = 'm';
        pname[4] = 'e';

        byte[] px = new byte[10];
        px[0] = 'x';
        px[1] = 'x';
        px[2] = '.';
        px[3] = '1';
        px[4] = '2';
        px[5] = '3';
        px[6] = '4';
        px[7] = '5';
        px[8] = '6';

        byte[] py = new byte[10];
        py[0] = 'x';
        py[1] = 'x';
        py[2] = 'x';
        py[3] = '.';
        py[4] = '1';
        py[5] = '2';
        py[6] = '3';
        py[7] = '4';
        py[8] = '5';
        py[9] = '6';

        byte[] sn = new byte[24];
        sn[0] = 'S';
        sn[1] = 'W';
        sn[2] = 'F';
        sn[3] = 'L';
        sn[4] = 'B';
        sn[5] = '-';
        sn[6] = '0';
        sn[7] = '0';
        sn[8] = '0';
        sn[9] = '0';
        sn[10] = '0';
        sn[11] = '0';
        sn[12] = '0';
        sn[13] = '0';
        sn[14] = '-';
        sn[15] = '0';
        sn[16] = '0';
        sn[17] = '0';
        sn[18] = '0';
        sn[19] = '-';
        sn[20] = '0';
        sn[21] = '0';
        sn[22] = '0';
        sn[23] = '0';

        byte period = 15;
        byte samplingTime = 4;
        byte sampleRate = 4;

        byte[] serverUrl = new byte[32];
        serverUrl[0] = 't';
        serverUrl[1] = 'h';
        serverUrl[2] = 'i';
        serverUrl[3] = 'n';
        serverUrl[4] = 'g';
        serverUrl[5] = 's';
        serverUrl[6] = 'w';
        serverUrl[7] = 'a';
        serverUrl[8] = 'r';
        serverUrl[9] = 'e';
        serverUrl[10] = '.';
        serverUrl[11] = 'c';
        serverUrl[12] = 'o';
        serverUrl[13] = '.';
        serverUrl[14] = 'k';
        serverUrl[15] = 'r';

        byte[] serverPort = new byte[4];
        serverPort[0] = '0';
        serverPort[1] = '0';
        serverPort[2] = '0';
        serverPort[3] = '0';

        byte[] dbUrl = new byte[32];
        dbUrl[0] = 't';
        dbUrl[1] = 'h';
        dbUrl[2] = 'i';
        dbUrl[3] = 'n';
        dbUrl[4] = 'g';
        dbUrl[5] = 's';
        dbUrl[6] = 'w';
        dbUrl[7] = 'a';
        dbUrl[8] = 'r';
        dbUrl[9] = 'e';
        dbUrl[10] = '.';
        dbUrl[11] = 'c';
        dbUrl[12] = 'o';
        dbUrl[13] = '.';
        dbUrl[14] = 'k';
        dbUrl[15] = 'r';

        byte[] dbPort = new byte[4];
        dbPort[0] = '0';
        dbPort[1] = '0';
        dbPort[2] = '0';
        dbPort[3] = '0';

        byte radioTime = 1;
        byte baudrate = 19;

        byte[] inputStream = new byte[169];
        System.arraycopy(recordTime1, 0, inputStream, 0, 4);
        System.arraycopy(recordTime2, 0, inputStream, 4, 4);
        System.arraycopy(recordTime3, 0, inputStream, 8, 4);
        System.arraycopy(fmRadio, 0, inputStream, 12, 4);
        System.arraycopy(sid, 0, inputStream, 16, 16);
        System.arraycopy(pname, 0, inputStream, 32, 16);
        System.arraycopy(px, 0, inputStream, 48, 10);
        System.arraycopy(py, 0, inputStream, 58, 10);
        System.arraycopy(sn, 0, inputStream, 68, 24);
        inputStream[92] = period;
        inputStream[93] = samplingTime;
        inputStream[94] = sampleRate;
        System.arraycopy(serverUrl, 0, inputStream, 95, 32);
        System.arraycopy(serverPort, 0, inputStream, 127, 4);
        System.arraycopy(dbUrl, 0, inputStream, 131, 32);
        System.arraycopy(dbPort, 0, inputStream, 163, 4);
        inputStream[167] = radioTime;
        inputStream[168] = baudrate;

        PreInstallResponse response = new PreInstallResponse(inputStream);

        assertThat(response.serialize().length).isEqualTo(169);
    }
}
