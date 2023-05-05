package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo
        WebSeries series = webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName());
        if(series != null){
            throw new Exception("Series is already present");
        }
        ProductionHouse productionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();
        if(productionHouse != null){
            WebSeries webSeries = new WebSeries();
            webSeries.setSeriesName(webSeriesEntryDto.getSeriesName());
            webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
            webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());
            webSeries.setRating(webSeriesEntryDto.getRating());
            productionHouse.getWebSeriesList().add(webSeries);

            double rating = 0;
            for(WebSeries series1 : productionHouse.getWebSeriesList()){
                rating += series1.getRating();
            }
            productionHouse.setRatings(rating/productionHouse.getWebSeriesList().size());
            webSeries.setProductionHouse(productionHouse);

            ProductionHouse saved = productionHouseRepository.save(productionHouse);
            return saved.getId();
        }
        return -1;
    }
}
