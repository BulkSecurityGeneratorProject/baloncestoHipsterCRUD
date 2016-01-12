package com.example.ligabaloncesto.web.rest;

import com.example.ligabaloncesto.Application;
import com.example.ligabaloncesto.domain.Arbitro;
import com.example.ligabaloncesto.repository.ArbitroRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.joda.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ArbitroResource REST controller.
 *
 * @see ArbitroResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ArbitroResourceTest {

    private static final String DEFAULT_NOMBRE = "SAMPLE_TEXT";
    private static final String UPDATED_NOMBRE = "UPDATED_TEXT";
    private static final String DEFAULT_APELLIDOS = "SAMPLE_TEXT";
    private static final String UPDATED_APELLIDOS = "UPDATED_TEXT";

    private static final LocalDate DEFAULT_FECHA_NACIMIENTO = new LocalDate(0L);
    private static final LocalDate UPDATED_FECHA_NACIMIENTO = new LocalDate();
    private static final String DEFAULT_LOCALIDAD = "SAMPLE_TEXT";
    private static final String UPDATED_LOCALIDAD = "UPDATED_TEXT";

    @Inject
    private ArbitroRepository arbitroRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restArbitroMockMvc;

    private Arbitro arbitro;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ArbitroResource arbitroResource = new ArbitroResource();
        ReflectionTestUtils.setField(arbitroResource, "arbitroRepository", arbitroRepository);
        this.restArbitroMockMvc = MockMvcBuilders.standaloneSetup(arbitroResource).setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        arbitro = new Arbitro();
        arbitro.setNombre(DEFAULT_NOMBRE);
        arbitro.setApellidos(DEFAULT_APELLIDOS);
        arbitro.setFechaNacimiento(DEFAULT_FECHA_NACIMIENTO);
        arbitro.setLocalidad(DEFAULT_LOCALIDAD);
    }

    @Test
    @Transactional
    public void createArbitro() throws Exception {
        int databaseSizeBeforeCreate = arbitroRepository.findAll().size();

        // Create the Arbitro

        restArbitroMockMvc.perform(post("/api/arbitros")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(arbitro)))
                .andExpect(status().isCreated());

        // Validate the Arbitro in the database
        List<Arbitro> arbitros = arbitroRepository.findAll();
        assertThat(arbitros).hasSize(databaseSizeBeforeCreate + 1);
        Arbitro testArbitro = arbitros.get(arbitros.size() - 1);
        assertThat(testArbitro.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testArbitro.getApellidos()).isEqualTo(DEFAULT_APELLIDOS);
        assertThat(testArbitro.getFechaNacimiento()).isEqualTo(DEFAULT_FECHA_NACIMIENTO);
        assertThat(testArbitro.getLocalidad()).isEqualTo(DEFAULT_LOCALIDAD);
    }

    @Test
    @Transactional
    public void getAllArbitros() throws Exception {
        // Initialize the database
        arbitroRepository.saveAndFlush(arbitro);

        // Get all the arbitros
        restArbitroMockMvc.perform(get("/api/arbitros"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(arbitro.getId().intValue())))
                .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())))
                .andExpect(jsonPath("$.[*].apellidos").value(hasItem(DEFAULT_APELLIDOS.toString())))
                .andExpect(jsonPath("$.[*].fechaNacimiento").value(hasItem(DEFAULT_FECHA_NACIMIENTO.toString())))
                .andExpect(jsonPath("$.[*].localidad").value(hasItem(DEFAULT_LOCALIDAD.toString())));
    }

    @Test
    @Transactional
    public void getArbitro() throws Exception {
        // Initialize the database
        arbitroRepository.saveAndFlush(arbitro);

        // Get the arbitro
        restArbitroMockMvc.perform(get("/api/arbitros/{id}", arbitro.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(arbitro.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE.toString()))
            .andExpect(jsonPath("$.apellidos").value(DEFAULT_APELLIDOS.toString()))
            .andExpect(jsonPath("$.fechaNacimiento").value(DEFAULT_FECHA_NACIMIENTO.toString()))
            .andExpect(jsonPath("$.localidad").value(DEFAULT_LOCALIDAD.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingArbitro() throws Exception {
        // Get the arbitro
        restArbitroMockMvc.perform(get("/api/arbitros/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateArbitro() throws Exception {
        // Initialize the database
        arbitroRepository.saveAndFlush(arbitro);

		int databaseSizeBeforeUpdate = arbitroRepository.findAll().size();

        // Update the arbitro
        arbitro.setNombre(UPDATED_NOMBRE);
        arbitro.setApellidos(UPDATED_APELLIDOS);
        arbitro.setFechaNacimiento(UPDATED_FECHA_NACIMIENTO);
        arbitro.setLocalidad(UPDATED_LOCALIDAD);
        

        restArbitroMockMvc.perform(put("/api/arbitros")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(arbitro)))
                .andExpect(status().isOk());

        // Validate the Arbitro in the database
        List<Arbitro> arbitros = arbitroRepository.findAll();
        assertThat(arbitros).hasSize(databaseSizeBeforeUpdate);
        Arbitro testArbitro = arbitros.get(arbitros.size() - 1);
        assertThat(testArbitro.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testArbitro.getApellidos()).isEqualTo(UPDATED_APELLIDOS);
        assertThat(testArbitro.getFechaNacimiento()).isEqualTo(UPDATED_FECHA_NACIMIENTO);
        assertThat(testArbitro.getLocalidad()).isEqualTo(UPDATED_LOCALIDAD);
    }

    @Test
    @Transactional
    public void deleteArbitro() throws Exception {
        // Initialize the database
        arbitroRepository.saveAndFlush(arbitro);

		int databaseSizeBeforeDelete = arbitroRepository.findAll().size();

        // Get the arbitro
        restArbitroMockMvc.perform(delete("/api/arbitros/{id}", arbitro.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Arbitro> arbitros = arbitroRepository.findAll();
        assertThat(arbitros).hasSize(databaseSizeBeforeDelete - 1);
    }
}
