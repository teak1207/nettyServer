package com.devgong.nettyserver.protocol;

import com.devgong.nettyserver.protocol.preinstall.PreInstallReportRequest;
import com.devgong.nettyserver.protocol.preinstall.PreInstallRequest;
import com.devgong.nettyserver.protocol.preinstall.PreInstallResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PacketTest {

    @Test
    void makePreInstallRequestPacket() {
        byte flag = 'A';
        byte[] sensorId = new byte[24];
        sensorId[0] = 'S';
        sensorId[1] = 'e';
        sensorId[2] = 'n';
        sensorId[3] = 's';
        sensorId[4] = 'o';
        sensorId[5] = 'r';
        sensorId[6] = ' ';
        sensorId[7] = 'I';
        sensorId[8] = 'D';
        byte[] dateTime = new byte[15];
        dateTime[0] = '2';
        dateTime[1] = '0';
        dateTime[2] = '2';
        dateTime[3] = '2';
        dateTime[4] = '0';
        dateTime[5] = '9';
        dateTime[6] = '1';
        dateTime[7] = '1';
        dateTime[8] = ' ';
        dateTime[9] = '1';
        dateTime[10] = '6';
        dateTime[11] = '2';
        dateTime[12] = '6';
        dateTime[13] = '1';
        dateTime[14] = '0';

        byte requestType = 0x44;

        byte[] parameterLength = new byte[4];
        parameterLength[0] = 31;

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

        byte[] checksum = new byte[2];
        checksum[0] = 11;
        checksum[1] = 42;

        byte[] inputStream = new byte[75];
//        inputStream[0] = flag;
        System.arraycopy(sensorId, 0, inputStream, 0, 24);
        System.arraycopy(dateTime, 0, inputStream, 24, 15);
        inputStream[39] = requestType;
        System.arraycopy(parameterLength, 0, inputStream, 40, 4);
        System.arraycopy(modemPhoneNumber, 0, inputStream, 44, 16);
        System.arraycopy(debugMessage, 0, inputStream, 60, 13);
        System.arraycopy(checksum, 0, inputStream, 73, 2);

        Packet<PreInstallRequest> packet = new Packet<>(inputStream, PreInstallRequest.class);
        assertThat(packet.getParameter().serialize().length).isEqualTo(29);
        assertThat(packet.serialize().length).isEqualTo(75);
    }

    @Test
    void makePreInstallResponsePacket() {
        byte flag = 'A';
        byte[] sensorId = new byte[24];
        sensorId[0] = 'S';
        sensorId[1] = 'e';
        sensorId[2] = 'n';
        sensorId[3] = 's';
        sensorId[4] = 'o';
        sensorId[5] = 'r';
        sensorId[6] = ' ';
        sensorId[7] = 'I';
        sensorId[8] = 'D';
        byte[] dateTime = new byte[15];
        dateTime[0] = '2';
        dateTime[1] = '0';
        dateTime[2] = '2';
        dateTime[3] = '2';
        dateTime[4] = '0';
        dateTime[5] = '9';
        dateTime[6] = '1';
        dateTime[7] = '1';
        dateTime[8] = ' ';
        dateTime[9] = '1';
        dateTime[10] = '6';
        dateTime[11] = '2';
        dateTime[12] = '6';
        dateTime[13] = '1';
        dateTime[14] = '0';

        byte requestType = 0x44;

        byte[] parameterLength = new byte[4];
        parameterLength[0] = 31;

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
        byte baudrate = 1;

        byte[] checksum = new byte[2];
        checksum[0] = 40;
        checksum[1] = 28;

        byte[] inputStream = new byte[216];
        inputStream[0] = flag;
        System.arraycopy(sensorId, 0, inputStream, 1, 24);
        System.arraycopy(dateTime, 0, inputStream, 25, 15);
        inputStream[40] = requestType;
        System.arraycopy(parameterLength, 0, inputStream, 41, 4);
        System.arraycopy(recordTime1, 0, inputStream, 45, 4);
        System.arraycopy(recordTime2, 0, inputStream, 49, 4);
        System.arraycopy(recordTime3, 0, inputStream, 53, 4);
        System.arraycopy(fmRadio, 0, inputStream, 57, 4);
        System.arraycopy(sid, 0, inputStream, 61, 16);
        System.arraycopy(pname, 0, inputStream, 77, 16);
        System.arraycopy(px, 0, inputStream, 93, 10);
        System.arraycopy(py, 0, inputStream, 103, 10);
        System.arraycopy(sn, 0, inputStream, 113, 24);
        inputStream[137] = period;
        inputStream[138] = samplingTime;
        inputStream[139] = sampleRate;
        System.arraycopy(serverUrl, 0, inputStream, 140, 32);
        System.arraycopy(serverPort, 0, inputStream, 172, 4);
        System.arraycopy(dbUrl, 0, inputStream, 176, 32);
        System.arraycopy(dbPort, 0, inputStream, 208, 4);
        inputStream[212] = radioTime;
        inputStream[213] = baudrate;
        System.arraycopy(checksum, 0, inputStream, 214, 2);

        Packet<PreInstallResponse> packet = new Packet<>(inputStream, PreInstallResponse.class);
        assertThat(packet.getParameter().serialize().length).isEqualTo(169);
        assertThat(packet.serialize().length).isEqualTo(216);
    }


    @Test
    void makePreInstallReportRequestPacket() {
        byte flag = 'A';
        byte[] sensorId = new byte[24];
        sensorId[0] = 'S';
        sensorId[1] = 'e';
        sensorId[2] = 'n';
        sensorId[3] = 's';
        sensorId[4] = 'o';
        sensorId[5] = 'r';
        sensorId[6] = ' ';
        sensorId[7] = 'I';
        sensorId[8] = 'D';
        byte[] dateTime = new byte[15];
        dateTime[0] = '2';
        dateTime[1] = '0';
        dateTime[2] = '2';
        dateTime[3] = '2';
        dateTime[4] = '0';
        dateTime[5] = '9';
        dateTime[6] = '1';
        dateTime[7] = '1';
        dateTime[8] = ' ';
        dateTime[9] = '1';
        dateTime[10] = '6';
        dateTime[11] = '2';
        dateTime[12] = '6';
        dateTime[13] = '1';
        dateTime[14] = '0';

        byte requestType = 0x44;

        byte[] parameterLength = new byte[4];
        parameterLength[0] = 31;

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

        byte[] checksum = new byte[2];
        checksum[0] = 39;
        checksum[1] = 109;

        byte[] inputStream = new byte[221];
        inputStream[0] = flag;
        System.arraycopy(sensorId, 0, inputStream, 1, 24);
        System.arraycopy(dateTime, 0, inputStream, 25, 15);
        inputStream[40] = requestType;
        System.arraycopy(parameterLength, 0, inputStream, 41, 4);
        System.arraycopy(debugMessage, 0, inputStream, 45, 13);
        System.arraycopy(recordTime1, 0, inputStream, 58, 4);
        System.arraycopy(recordTime2, 0, inputStream, 62, 4);
        System.arraycopy(recordTime3, 0, inputStream, 66, 4);
        System.arraycopy(fmRadio, 0, inputStream, 70, 4);
        System.arraycopy(firmwareVersion, 0, inputStream, 74, 6);
        System.arraycopy(batteryValue, 0, inputStream, 80, 6);
        inputStream[86] = modemRssi;
        System.arraycopy(deviceStatus, 0, inputStream, 87, 2);
        inputStream[89] = samplingTime;
        System.arraycopy(px, 0, inputStream, 90, 10);
        System.arraycopy(py, 0, inputStream, 100, 10);
        System.arraycopy(pname, 0, inputStream, 110, 16);
        System.arraycopy(sid, 0, inputStream, 126, 16);
        inputStream[142] = period;
        System.arraycopy(serverUrl, 0, inputStream, 143, 32);
        System.arraycopy(serverPort, 0, inputStream, 175, 4);
        System.arraycopy(dbUrl, 0, inputStream, 179, 32);
        System.arraycopy(dbPort, 0, inputStream, 211, 4);
        inputStream[215] = radioTime;
        inputStream[216] = baudrate;
        inputStream[217] = baudrateNext;
        inputStream[218] = pcbVersion;
        System.arraycopy(checksum, 0, inputStream, 219, 2);

        Packet<PreInstallReportRequest> packet = new Packet<>(inputStream, PreInstallReportRequest.class);
        assertThat(packet.getParameter().serialize().length).isEqualTo(174);
        assertThat(packet.serialize().length).isEqualTo(221);
    }
}
