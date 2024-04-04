package codehanzoom.greenwalk.partner.dto;

import codehanzoom.greenwalk.partner.domain.Partner;
import lombok.Getter;

@Getter
public class PartnerDto {

    private Long id;
    private String name;
    private String introduction;
    private int totalDonationAmount ;

    public PartnerDto(Partner partner) {
        this.id = partner.getId();
        this.name = partner.getName();
        this.introduction = partner.getIntroduction();
        this.totalDonationAmount = partner.getTotalDonationAmount();
    }

}
