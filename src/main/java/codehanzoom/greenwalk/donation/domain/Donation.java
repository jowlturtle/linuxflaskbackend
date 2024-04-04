package codehanzoom.greenwalk.donation.domain;

import codehanzoom.greenwalk.partner.domain.Partner;
import codehanzoom.greenwalk.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Donation {

    @Id
    @GeneratedValue
    @Column(name = "donation_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sponsor_id")
    private Partner partner;

    private int donationAmount;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    public final static Donation createDonation(User user, Partner partner, int donationAmount) {
//        Donation donation = new Donation();
        Donation donation = Donation.builder()
                .user(user)
                .partner(partner)
                .donationAmount(donationAmount)
                .createDate(LocalDateTime.now()).build();

//        donation.setUser(user);
//        donation.setPartner(partner);
//        donation.setDonationAmount(donationAmount);
//        donation.setCreateDate(LocalDateTime.now());

        partner.addDonationAmount(donationAmount); // 기부처에 기부총액 증가
        user.removeTotalPoint(donationAmount); // 유저 포인트 감소

        return donation;
    }

//    //==생성 메서드==//
//    public static Donation createDonation(User user, Partner partner, int donationAmount) {
//        Donation donation = new Donation();
//        donation.setUser(user);
//        donation.setPartner(partner);
//        donation.setDonationAmount(donationAmount);
//        donation.setCreateDate(LocalDateTime.now());
//
//        partner.addDonationAmount(donationAmount); // 기부처에 기부총액 증가
//        user.removeTotalPoint(donationAmount); // 유저 포인트 감소
//
//        return donation;
//    }

//    public static void

}