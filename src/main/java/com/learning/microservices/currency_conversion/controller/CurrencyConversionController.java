package com.learning.microservices.currency_conversion.controller;

import com.learning.microservices.currency_conversion.bean.CurrencyConversion;
import com.learning.microservices.currency_conversion.proxy.CurrencyExchangeProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;

@RestController
public class CurrencyConversionController {

    @Autowired
    private Environment environment;

    @Autowired
    private CurrencyExchangeProxy currencyExchangeProxy;


    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion getCurrencyConversion(@PathVariable String from, @PathVariable String to, @PathVariable String quantity){

        HashMap<String,String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to",to);

        ResponseEntity<CurrencyConversion> result;
        result = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversion.class, uriVariables);

        CurrencyConversion currencyConversion = result.getBody();

        CurrencyConversion currencyConversion1 = new CurrencyConversion(currencyConversion.getId(), from, to, currencyConversion.getConversionMultiple(), new BigDecimal(quantity), currencyConversion.getConversionMultiple().multiply(new BigDecimal(quantity)),"");
        String port = environment.getProperty("local.server.port");
        currencyConversion1.setEnvironment(port);
        return currencyConversion1;
    }


    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion getCurrencyConversionUsingFeign(@PathVariable String from, @PathVariable String to, @PathVariable String quantity){

       CurrencyConversion currencyConversion = currencyExchangeProxy.retrieveExchangeValue(from, to);

        CurrencyConversion currencyConversion1 = new CurrencyConversion(currencyConversion.getId(), from, to, currencyConversion.getConversionMultiple(), new BigDecimal(quantity), currencyConversion.getConversionMultiple().multiply(new BigDecimal(quantity)), currencyConversion.getEnvironment());
        //String port = environment.getProperty("local.server.port");
        //currencyConversion1.getEnvironment();
        return currencyConversion1;
    }


}
