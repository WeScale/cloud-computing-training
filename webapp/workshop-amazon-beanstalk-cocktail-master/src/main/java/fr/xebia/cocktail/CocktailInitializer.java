/*
 * Copyright 2013  Séven Le Mesle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package fr.xebia.cocktail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;

/**
 * Created by slemesle on 02/10/15.
 */
@Service
public class CocktailInitializer {


    private final String urlPrefix;
    private final CocktailRestRepository cocktailsRepository;

    /**
	 *
	 */
	@Autowired
	public CocktailInitializer(@Value("${aws_s3_bucket_base_url}") String urlPrefix, CocktailRestRepository cocktailsRepository) {
        this.urlPrefix = urlPrefix;
        this.cocktailsRepository = cocktailsRepository;

        Assert.notNull(cocktailsRepository, "CocktailRestRepository must not be null!");

		if (cocktailsRepository.count() != 0) {
			return;
		}
        Cocktail sexOB = buildSexOnTheBeachCocktail();
        Cocktail longI = buildLongIslandCocktail();

		cocktailsRepository.save(Arrays.asList(sexOB, longI));
	}




     protected Cocktail buildSexOnTheBeachCocktail() {
        Cocktail sexOnTheBeach = new Cocktail()
                .withName("Sex On The Beach")
                .withIngredient("1 shot", "vodka")
                .withIngredient("1 shot", "peach schnapps (archers)")
                .withIngredient("200 ml", "orange juice")
                .withIngredient("200 ml", "cranberry juice")
                .withIngredient("2 shots", "raspberry syrup")
                .withPhotoUrl(urlPrefix+"/4703755392347885371.jpg")
                .withSourceUrl("http://www.cocktailmaking.co.uk/displaycocktail.php/321-Sex-On-The-Beach")
                .withInstructions(
                        "Add ice to glass pour in shot of vodka add peach shnapps mix with orange, cranberry and raspberry\n" //
                                + "\n" //
                                + "Serve with an umbrella and a mixer stick and a fancy straw and an orange slice on side of "
                                + "glass this one is gorgeous can't believe you don't already have it on here!");
        return sexOnTheBeach;
    }

    protected Cocktail buildLongIslandCocktail() {
        Cocktail longIslandIcedTea = new Cocktail()
                .withName("Long Island Iced tea")
                .withIngredient("1 Measure", "vodka")
                .withIngredient("1 Measure", "gin")
                .withIngredient("1 Measure", "white rum")
                .withIngredient("1 Measure", "tequila")
                .withIngredient("1 Measure", "triple sec")
                .withIngredient("3 measures", "orange juice")
                .withIngredient("to topp up the glass", "coke")
                .withPhotoUrl(urlPrefix+"/6762530443361434570.jpg")
                .withSourceUrl("http://www.cocktailmaking.co.uk/displaycocktail.php/1069-Long-Island-Iced-tea")
                .withInstructions(
                        "In a tall glass , add ice and all the ingredients and stir well. It should have the appearance of cloudy tea. Top with a piece of lemon\n"
                                + "\n"
                                + "Very yummy & very very deceiving. It will get you hammered after only about 2 so drink with caution");
        return longIslandIcedTea;
    }
}
