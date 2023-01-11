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
    private final ConcurrentMap<Integer, DataSequence> dataSequenceManagingMap = new ConcurrentHashMap<>();

    @Getter
    @RequiredArgsConstructor
    static class DataSequence {
        private final Integer sequence;
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

    public void enrollDataSequence(Integer cid, Integer fnum, LocalDateTime now) {
        dataSequenceManagingMap.put(cid, new DataSequence(fnum, now));
        log.info("등록 : {} - {} - {} ",cid,fnum,now);
    }

    public void decrementDataSequence(Integer cid, String sid, String sn, LocalDateTime now) {
        // TODO : decrement 하려고 했는데, 메뉴판에 등록되어있지 않은 경우 어떻게 처리할까?, Tube
        DataSequence beforeSequence = dataSequenceManagingMap.get(cid);
        DataSequence afterSequence = beforeSequence.decrement(now);
        if (afterSequence.sequence == 0) {
            dataSequenceManagingMap.remove(cid);
            log.info("cid : {} is removed", cid);
            dataUpdateRepository.updateCompleteTime(cid, sid, sn);
        } else {
            dataSequenceManagingMap.put(cid, afterSequence);
            log.info("cid : {} is decremented {}", cid, afterSequence.getSequence());
        }
    }

    // 매 분 정각마다 실행
    @Scheduled(cron = "5 * * * * *")
    public void refreshDataSource() {
        Set<Map.Entry<Integer, DataSequence>> dataSequences = dataSequenceManagingMap.entrySet();
        log.info("리프레시 시작");
        LocalDateTime now = LocalDateTime.now();
        for (Map.Entry<Integer, DataSequence> dataSequence : dataSequences) {
            log.info("검사해보자 => key: {} , value: {}", dataSequence.getKey(), dataSequence.getValue().getSequence());
            if (dataSequence.getValue().isDeprecated(now)) {
                log.info("Deprecated 되었음! => key: {}, value: {}", dataSequence.getKey(), dataSequence.getValue().getSequence());
                dataSequenceManagingMap.remove(dataSequence.getKey());
            }
        }
    }
    @Scheduled(cron = "0 0 0/1 * * *")
    public void hourCheck(){
        log.info("한시간 지났음");
    }

    @Scheduled(cron = "0 5 * * * *")
    public void fiveMinCheck(){
        log.info("5분v 지났음");
    }


}
