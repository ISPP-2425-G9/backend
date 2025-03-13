package com.caronte.caronte.receiver;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.util.LongRepository;

@Repository
public interface ReceiverRepository extends LongRepository<Receiver> {

    //List<Receiver> findByObituaryId(Long obituaryId);

    //Optional<Receiver> findByEmailAndObituary(String email, Obituary obituary);

    void deleteByObituary(Obituary obituary);

    List<Receiver> findByObituary(Obituary obituary);
}
