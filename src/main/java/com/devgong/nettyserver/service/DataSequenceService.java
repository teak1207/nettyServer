package com.devgong.nettyserver.service;

import com.devgong.nettyserver.repository.DataUpdateRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSequenceService {
    private final DataUpdateRepository dataUpdateRepository;
    private final ConcurrentMap<String, DataSequence> dataSequenceManagingMap = new ConcurrentHashMap<>();

    @Getter
    @RequiredArgsConstructor
    static class DataSequence {
        private final Integer sequence;  // frameCount number
        private final LocalDateTime accessTime;

        public DataSequence decrement(LocalDateTime accessTime) {
            return new DataSequence(sequence - 1, accessTime);
        }

        public boolean isDeprecated(LocalDateTime now) {
//            return now.isAfter(accessTime.plusMinutes(10));
            return now.isAfter(accessTime.plusMinutes(2));
//            return accessTime.plusMinutes(10).isBefore(now);
        }
    }

    public void enrollDataSequence(String serialNumber, Integer fnum, LocalDateTime now) {
        dataSequenceManagingMap.put(serialNumber, new DataSequence(fnum, now));
        log.info("[REQUEST][ENROLL] :  serialNumber : {} - FrameCount : {} - RealTime : {} ", serialNumber, fnum, now);
    }

    public void decrementDataSequence(String cid, String sid, String sn, LocalDateTime now, int temp) {
        // TODO : decrement 하려고 했는데, 메뉴판에 등록되어있지 않은 경우 어떻게 처리할까?, Tube

        log.info("sidchk6 :  {}", cid);
        log.info("sidchk6 :  {}", sid);
        log.info("sidchk6 :  {}", sn);
        log.info("sidchk6 :  {}", temp);



        // memo : request에서 등록한 값을 담음.
        DataSequence beforeSequence = dataSequenceManagingMap.get(cid);
        log.info("sidchk4  :{}" , beforeSequence);

        // memo : beforeSequence 의 sequence -1
        DataSequence afterSequence = beforeSequence.decrement(now);
        log.info("sidchk5  :{}" , afterSequence);

        //memo : 만약 sequence == 0 이라면
        if (afterSequence.sequence == 0) {
            dataSequenceManagingMap.remove(cid);
            log.info("[REQUEST][SEQ]: cid {} is removed", cid);

            //memo : leak_send_data 의 complete / completeTime 을 update.
            dataUpdateRepository.updateCompleteTime(Integer.valueOf(cid), sid, sn);

        } else {
            dataSequenceManagingMap.put(cid, afterSequence);
            log.info("[DATA][SEQ]: {} is decremented {}", cid, afterSequence.getSequence());
        }
    }

    //check : 메뉴판 관리자, 매 분 정각마다 실행
    @Scheduled(cron = "0 * * * * *")
    public void refreshDataSource() {
        Set<Map.Entry<String, DataSequence>> dataSequences = dataSequenceManagingMap.entrySet();
        log.info("리프레시 시작");
        LocalDateTime now = LocalDateTime.now();
        for (Map.Entry<String, DataSequence> dataSequence : dataSequences) {

            log.info("[REQUEST][SEQ]: 검사시작 => key: {} , value: {}", dataSequence.getKey(), dataSequence.getValue().getSequence());
            if (dataSequence.getValue().isDeprecated(now)) {
                log.info("[REQUEST][SEQ]: Deprecated 되었음! => key: {}, value: {}", dataSequence.getKey(), dataSequence.getValue().getSequence());
                log.info("----------------------------------------");

                dataSequenceManagingMap.remove(dataSequence.getKey());
            }
        }
    }
}
