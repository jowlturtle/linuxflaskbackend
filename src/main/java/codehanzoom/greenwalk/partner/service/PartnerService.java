package codehanzoom.greenwalk.partner.service;

import codehanzoom.greenwalk.partner.domain.Partner;
import codehanzoom.greenwalk.partner.repository.PartnerRepository;
import codehanzoom.greenwalk.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final UserRepository userRepository;

    public List<Partner> findPartners() {
        return partnerRepository.findAll();
    }

    public Partner findOne(Long sponsorId) {
        return partnerRepository.findById(sponsorId).get();
    }

    @Transactional
    public void savePartner(Partner partner) {
        partnerRepository.save(partner);
    }

    @Transactional
    public void updateDonationAmount(Long partnerId, int donateAmount) {
        Partner findPartner = partnerRepository.findById(partnerId).get();
        findPartner.addDonationAmount(donateAmount);
    }
}
