package com.tfg.app.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tfg.app.model.Util;
import com.tfg.app.repository.UtilRepository;

@Service
public class UtilService {

    @Autowired
    private UtilRepository utilRepository;

    public void save(Util util) {
        this.utilRepository.save(util);
    }

    public Optional<Util> findById(Long id) {
        return utilRepository.findById(id);
    }

    public void delete(Util entity) {
        utilRepository.delete(entity);
    }

    public int getAppointmentsCompletedYesterday() {
        return utilRepository.getAppointmentsCompletedYesterday();
    }

    public int getNumPatientsYesterday() {
        return utilRepository.findNumPatientsYesterdayById(23L);
    }

    public int getNumPatientsTotal() {
        return utilRepository.findNumPatientsTotalById(23L);
    }

    public Util partialUpdate(Long id, Util utilupd) {
        Util util = findById(23L).get();
        if (utilupd.getAppointmentsCompletedYesterday() != 0){
            util.setAppointmentsCompletedYesterday(utilupd.getAppointmentsCompletedYesterday());
        }
        if (utilupd.getNumPatientsTotal() != 0){
            util.setNumPatientsTotal(utilupd.getNumPatientsTotal());
        }
        if (utilupd.getNumPatientsYesterday() != 0){
            util.setNumPatientsYesterday(utilupd.getNumPatientsYesterday());
        }

        Util updatedUtil = utilRepository.save(util);
        return updatedUtil;
    }
}
