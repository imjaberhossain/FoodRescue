package com.foodrescue.service;

import com.foodrescue.model.Organization;
import com.foodrescue.model.VerificationStatus;
import com.foodrescue.model.Volunteer;
import com.foodrescue.repository.OrganizationRepository;
import com.foodrescue.repository.VolunteerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Manual NID/document verification for NGOs and Volunteers.
 * Only a user with users.is_admin = TRUE (set directly in the database, never through
 * the website) can reach these methods - see AdminController for the access check.
 */
@Service
public class AdminService {

    private final OrganizationRepository orgRepo;
    private final VolunteerRepository volunteerRepo;

    public AdminService(OrganizationRepository orgRepo, VolunteerRepository volunteerRepo) {
        this.orgRepo = orgRepo;
        this.volunteerRepo = volunteerRepo;
    }

    /** Every NGO/Volunteer whose document is still waiting for a decision. */
    @Transactional(readOnly = true)
    public List<VerificationRow> pendingVerifications() {
        List<VerificationRow> rows = new ArrayList<>();
        for (Organization o : orgRepo.findByVerificationStatusOrderByIdAsc(VerificationStatus.PENDING)) {
            rows.add(VerificationRow.forOrganization(o));
        }
        for (Volunteer v : volunteerRepo.findByVerificationStatusOrderByIdAsc(VerificationStatus.PENDING)) {
            rows.add(VerificationRow.forVolunteer(v));
        }
        return rows;
    }

    @Transactional
    public void decideOrganization(Long organizationId, VerificationStatus decision) {
        Organization o = orgRepo.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found."));
        o.setVerificationStatus(decision);
    }

    @Transactional
    public void decideVolunteer(Long volunteerId, VerificationStatus decision) {
        Volunteer v = volunteerRepo.findById(volunteerId)
                .orElseThrow(() -> new IllegalArgumentException("Volunteer not found."));
        v.setVerificationStatus(decision);
    }

    /** One row in the admin's review table - works the same whether it's an NGO or a Volunteer. */
    public static class VerificationRow {
        public Long id;              // organization id OR volunteer id (see 'type')
        public String type;          // "NGO" or "VOLUNTEER"
        public String name;
        public String email;
        public String phone;
        public String city;
        public String idDocumentType;
        public String idDocumentNumber;
        public String idDocumentPath;

        static VerificationRow forOrganization(Organization o) {
            VerificationRow r = new VerificationRow();
            r.id = o.getId();
            r.type = "NGO";
            r.name = o.getOrgName();
            r.email = o.getUser().getEmail();
            r.phone = o.getUser().getPhone();
            r.city = o.getCity();
            r.idDocumentType = o.getIdDocumentType();
            r.idDocumentNumber = o.getIdDocumentNumber();
            r.idDocumentPath = o.getIdDocumentPath();
            return r;
        }

        static VerificationRow forVolunteer(Volunteer v) {
            VerificationRow r = new VerificationRow();
            r.id = v.getId();
            r.type = "VOLUNTEER";
            r.name = v.getUser().getFullName();
            r.email = v.getUser().getEmail();
            r.phone = v.getUser().getPhone();
            r.city = v.getCity();
            r.idDocumentType = v.getIdDocumentType();
            r.idDocumentNumber = v.getIdDocumentNumber();
            r.idDocumentPath = v.getIdDocumentPath();
            return r;
        }
    }
}
