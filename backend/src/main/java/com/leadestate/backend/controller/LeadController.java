package com.leadestate.backend.controller;

import com.leadestate.backend.entity.Lead;
import com.leadestate.backend.repository.LeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    @Autowired
    private LeadRepository leadRepository;
    @GetMapping
    public List<Lead> getAllLeads() {
        return leadRepository.findAll();
    }
}