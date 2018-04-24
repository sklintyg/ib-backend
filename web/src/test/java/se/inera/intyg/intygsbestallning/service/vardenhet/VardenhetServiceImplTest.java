package se.inera.intyg.intygsbestallning.service.vardenhet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.intygsbestallning.persistence.model.VardenhetPreference;
import se.inera.intyg.intygsbestallning.persistence.repository.VardenhetPreferenceRepository;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.VardenhetPreferenceRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.VardenhetPreferenceResponse;

/**
 * Created by marced on 2018-04-24.
 */
@RunWith(MockitoJUnitRunner.class)
public class VardenhetServiceImplTest {

    private static final String HSA_ID = "HSA-123";
    private static final String ENHETSNAMN = "Enheten";
    private static final String POSTADRESS = "Gatan 3";
    private static final String POSTNUMMER = "123455";
    private static final String TELEFON = "011-22334455";
    private static final String EPOST = "enhet@vg.se";
    private static final String POSTORT = "Vårdinge";

    private static final String ENHETSNAMN_2 = "Ny-Enheten";
    private static final String POSTADRESS_2 = "Ny-Gatan 3";
    private static final String POSTNUMMER_2 = "0004444";
    private static final String TELEFON_2 = "044-55334455";
    private static final String EPOST_2 = "ny-enhet@vg.se";
    private static final String POSTORT_2 = "Ny-Vårdinge";
    private static final String STANDARDSVAR_2 = "Vi gör allt för er!";

    @Mock
    private VardenhetPreferenceRepository vardenhetPreferenceRepository;

    @Mock
    private HsaOrganizationsService hsaOrganizationsService;

    @InjectMocks
    private VardenhetServiceImpl testee;

    @Test
    public void testGetExistingVardEnhetPreference() throws Exception {
        VardenhetPreference expected = createVardenhetPreferenceSample();
        when(vardenhetPreferenceRepository.findByVardenhetHsaId(eq(HSA_ID))).thenReturn(Optional.of(expected));

        final VardenhetPreferenceResponse result = testee.getVardEnhetPreference(HSA_ID);

        assertEquals(expected.getVardenhetHsaId(), result.getVardenhetHsaId());
        assertEquals(expected.getMottagarNamn(), result.getMottagarNamn());
        assertEquals(expected.getAdress(), result.getAdress());
    }

    @Test
    public void testGetInitialVardEnhetPreference() throws Exception {
        when(vardenhetPreferenceRepository.findByVardenhetHsaId(eq(HSA_ID))).thenReturn(Optional.empty());
        // just echo back the argument to save()
        when(vardenhetPreferenceRepository.save(any(VardenhetPreference.class))).thenAnswer(
                invocation -> invocation.getArgument(0));
        Vardenhet expectedVardenhet = createVardEnhetSample();
        when(hsaOrganizationsService.getVardenhet(eq(HSA_ID))).thenReturn(expectedVardenhet);

        final VardenhetPreferenceResponse result = testee.getVardEnhetPreference(HSA_ID);

        assertEquals(HSA_ID, result.getVardenhetHsaId());
        assertEquals(ENHETSNAMN, result.getMottagarNamn());
        assertEquals(POSTADRESS, result.getAdress());
        assertEquals(POSTNUMMER, result.getPostnummer());
        assertEquals(POSTORT, result.getPostort());
        assertEquals(TELEFON, result.getTelefonnummer());
        assertEquals(EPOST, result.getEpost());
    }

    @Test
    public void testSetVardEnhetPreference() throws Exception {
        when(vardenhetPreferenceRepository.findByVardenhetHsaId(eq(HSA_ID))).thenReturn(Optional.of(createVardenhetPreferenceSample()));
        // just echo back the argument to save()
        when(vardenhetPreferenceRepository.save(any(VardenhetPreference.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        VardenhetPreferenceRequest request = createUpdateRequestSample();
        final VardenhetPreferenceResponse result = testee.setVardEnhetPreference(HSA_ID, request);

        assertEquals(request.getMottagarNamn(), result.getMottagarNamn());
        assertEquals(request.getAdress(), result.getAdress());
        assertEquals(request.getPostnummer(), result.getPostnummer());
        assertEquals(request.getPostort(), result.getPostort());
        assertEquals(request.getTelefonnummer(), result.getTelefonnummer());
        assertEquals(request.getEpost(), result.getEpost());
        assertEquals(request.getStandardsvar(), result.getStandardsvar());

    }

    private VardenhetPreferenceRequest createUpdateRequestSample() {
        VardenhetPreferenceRequest vpr = new VardenhetPreferenceRequest();
        vpr.setMottagarNamn(ENHETSNAMN_2);
        vpr.setAdress(POSTADRESS_2);
        vpr.setPostnummer(POSTNUMMER_2);
        vpr.setPostort(POSTORT_2);
        vpr.setTelefonnummer(TELEFON_2);
        vpr.setEpost(EPOST_2);
        vpr.setStandardsvar(STANDARDSVAR_2);
        return vpr;
    }

    private VardenhetPreference createVardenhetPreferenceSample() {
        VardenhetPreference vp = new VardenhetPreference();
        vp.setVardenhetHsaId(HSA_ID);
        vp.setMottagarNamn(ENHETSNAMN);
        vp.setAdress(POSTADRESS);
        vp.setPostnummer(POSTNUMMER);
        vp.setPostort(POSTORT);
        vp.setTelefonnummer(TELEFON);
        vp.setEpost(EPOST);
        return vp;
    }

    private Vardenhet createVardEnhetSample() {
        Vardenhet v = new Vardenhet(HSA_ID, ENHETSNAMN);
        v.setPostadress(POSTADRESS);
        v.setPostnummer(POSTNUMMER);
        v.setPostort(POSTORT);
        v.setTelefonnummer(TELEFON);
        v.setEpost(EPOST);
        return v;
    }
}
