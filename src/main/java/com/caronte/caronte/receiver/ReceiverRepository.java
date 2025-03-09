package com.caronte.caronte.receiver;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.util.LongRepository;

@Repository
public interface ReceiverRepository extends LongRepository<Receiver> {

    //List<Receiver> findByObituaryId(Long obituaryId);

    //Optional<Receiver> findByEmailAndObituary(String email, Obituary obituary);

    @Transactional
    void deleteByObituary(Obituary obituary);

    List<Receiver> findByObituary(Obituary obituary);
}
