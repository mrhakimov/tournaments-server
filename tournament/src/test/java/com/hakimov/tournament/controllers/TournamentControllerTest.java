package com.hakimov.tournament.controllers;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TournamentControllerTest {
//    private MockMvc tournamentMockMvc;
//
//    @InjectMocks
//    private TournamentController tournamentController;
//
//    @BeforeEach
//    void setUp() {
//        tournamentMockMvc = MockMvcBuilders.standaloneSetup(tournamentController).build();
//    }
//
//    @Test
//    public void testGetTournament() throws Exception {
//        tournamentMockMvc = MockMvcBuilders.standaloneSetup(tournamentController).build();
//        tournamentMockMvc.perform(
//                get("/api/tournaments/1")
//        )
//                .andExpect(status().isBadRequest());
//    }
}
