package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setUser(user);
        subscription.setStartSubscriptionDate(new Date());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        SubscriptionType subscriptionType = subscriptionEntryDto.getSubscriptionType();
        int amount = 0;
        if(subscriptionType.equals(SubscriptionType.BASIC)){
            amount = 500 + (200*subscriptionEntryDto.getNoOfScreensRequired());
        } else if (subscriptionType.equals(SubscriptionType.PRO)) {
            amount = 800 + (250*subscriptionEntryDto.getNoOfScreensRequired());
        }
        else if(subscriptionType.equals(SubscriptionType.ELITE)){
            amount = 1000 + (350*subscriptionEntryDto.getNoOfScreensRequired());
        }
        subscription.setTotalAmountPaid(amount);
        user.setSubscription(subscription);
        userRepository.save(user);
        return amount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();
        if(subscription.getSubscriptionType().equals(SubscriptionType.ELITE)){
            throw new Exception("Already the best Subscription");
        }
        int amount = 0;
        if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC)){
            int newAmount = 800 + subscription.getNoOfScreensSubscribed() * 250;
            amount = newAmount - subscription.getTotalAmountPaid();
            subscription.setSubscriptionType(SubscriptionType.PRO);
        }
        else if(subscription.getSubscriptionType().equals(SubscriptionType.PRO)){
            int newAmount = 1000 + subscription.getNoOfScreensSubscribed() * 350;
            amount = newAmount - subscription.getTotalAmountPaid();
            subscription.setSubscriptionType(SubscriptionType.ELITE);
        }
        user.setSubscription(subscription);
        userRepository.save(user);
        return amount;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        int total = 0;
        for(Subscription subscription : subscriptions){
            total += subscription.getTotalAmountPaid();
        }
        return total;
    }

}
