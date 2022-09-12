package com.devgong.nettyserver.protocol.preinstall;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PreInstallReportRequestTest {

    @Test
    void makeReportRequestTest() {
        byte[] debugMessage = new byte[13];
        debugMessage[0] = '0';
        debugMessage[1] = '0';
        debugMessage[2] = ' ';
        debugMessage[3] = 'N';
        debugMessage[4] = 'O';
        debugMessage[5] = 'N';
        debugMessage[6] = 'E';

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

        byte[] firmwareVersion = new byte[6];
        firmwareVersion[0] = '0';
        firmwareVersion[1] = '1';
        firmwareVersion[2] = '.';
        firmwareVersion[3] = '0';
        firmwareVersion[4] = '0';
        firmwareVersion[5] = '0';

        byte[] batteryValue = new byte[6];
        batteryValue[0] = '5';
        batteryValue[1] = '.';
        batteryValue[2] = '0';
        batteryValue[3] = '0';
        batteryValue[4] = '0';
        batteryValue[5] = '0';

        byte modemRssi = 1;

        byte[] deviceStatus = new byte[2];
        deviceStatus[0] = '0';
        deviceStatus[1] = '0';

        byte samplingTime = 1;

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

        byte[] pname = new byte[16];
        pname[0] = 'p';
        pname[1] = 'n';
        pname[2] = 'a';
        pname[3] = 'm';
        pname[4] = 'e';

        byte[] sid = new byte[16];
        sid[0] = 's';
        sid[1] = 'i';
        sid[2] = 'd';

        byte period = 1;

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
        byte baudrateNext = 1;
        byte pcbVersion = 5;

        byte[] inputStream = new byte[174];
        System.arraycopy(debugMessage, 0, inputStream, 0, 13);
        System.arraycopy(recordTime1, 0, inputStream, 13, 4);
        System.arraycopy(recordTime2, 0, inputStream, 17, 4);
        System.arraycopy(recordTime3, 0, inputStream, 21, 4);
        System.arraycopy(fmRadio, 0, inputStream, 25, 4);
        System.arraycopy(firmwareVersion, 0, inputStream, 29, 6);
        System.arraycopy(batteryValue, 0, inputStream, 35, 6);
        inputStream[41] = modemRssi;
        System.arraycopy(deviceStatus, 0, inputStream, 42, 2);
        inputStream[44] = samplingTime;
        System.arraycopy(px, 0, inputStream, 45, 10);
        System.arraycopy(py, 0, inputStream, 55, 10);
        System.arraycopy(pname, 0, inputStream, 65, 16);
        System.arraycopy(sid, 0, inputStream, 81, 16);
        inputStream[97] = period;
        System.arraycopy(serverUrl, 0, inputStream, 98, 32);
        System.arraycopy(serverPort, 0, inputStream, 130, 4);
        System.arraycopy(dbUrl, 0, inputStream, 134, 32);
        System.arraycopy(dbPort, 0, inputStream, 166, 4);
        inputStream[170] = radioTime;
        inputStream[171] = baudrate;
        inputStream[172] = baudrateNext;
        inputStream[173] = pcbVersion;

        PreInstallReportRequest request = new PreInstallReportRequest(inputStream);

        assertThat(request.serialize().length).isEqualTo(174);
    }
}

