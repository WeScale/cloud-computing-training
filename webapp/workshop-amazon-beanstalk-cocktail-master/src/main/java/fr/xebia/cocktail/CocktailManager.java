/*
 * Copyright 2008-2012 Xebia and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.xebia.cocktail;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedMetric;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.support.MetricType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import static org.springframework.util.StringUtils.hasLength;


/**
 * Main controller of the application.
 *
 * @author <a href="mailto:cleclerc@xebia.fr">Cyrille Le Clerc</a>
 */
@ManagedResource("cocktail:type=CocktailManager,name=CocktailManager")
@Controller
public class CocktailManager {

    @Inject
    private CocktailRepository cocktailRepository;

    @Inject
    private IngredientsRepository ingredientsRepository;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private AmazonS3RestService fileStorageService;

    private final AtomicInteger addedCommentCount = new AtomicInteger();

    @RequestMapping(value = "/cocktail/{id}/comment", method = RequestMethod.POST)
    public String addComment(@PathVariable String id, @RequestParam("comment") String comment, HttpServletRequest request) {

        Cocktail cocktail = cocktailRepository.findOne(id);
        if (cocktail == null) {
            throw new ResourceNotFoundException(id);
        }
        logger.debug("Add comment: '{}' to {}", comment, cocktail);
        cocktail.getComments().add(comment);
        cocktailRepository.save(cocktail);
        addedCommentCount.incrementAndGet();

        return "redirect:/cocktail/{id}";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/cocktail")
    public String create(Cocktail cocktail, BindingResult result) {
        if (result.hasErrors()) {
            return "cocktail/create-form";
        }

        cocktailRepository.save(cocktail);

        return "redirect:/cocktail/" + cocktail.getId();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/cocktail/create-form")
    public String displayCreateForm(Model model) {
        model.addAttribute(new Cocktail());
        return "cocktail/create-form";
    }

    @RequestMapping(value = "/cocktail/{id}/edit-form", method = RequestMethod.GET)
    public String displayEditForm(@PathVariable String id, Model model) {
        Cocktail cocktail = cocktailRepository.findOne(id);
        if (cocktail == null) {
            throw new ResourceNotFoundException(id);
        }
        model.addAttribute(cocktail);
        return "cocktail/edit-form";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/cocktail/suggest/ingredient")
    @ResponseBody
    public Collection<String> suggestCocktailIngredientWord(@RequestParam("term") String term) {
        Collection<String> words =
        Collections2.transform(this.ingredientsRepository.findByNameStartingWith(term), new Function<Ingredient, String>() {
            @Nullable @Override public String apply(Ingredient ingredient) {
                return ingredient.getName();
            }
        });
        logger.trace("autocomplete word for {}:{}", term, words);
        return words;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/cocktail/suggest/name")
    @ResponseBody
    public Collection<String> suggestCocktailNameWord(@RequestParam("term") String term) {
        Collection<String> words = Collections2.transform(this.cocktailRepository.findByNameStartingWith(term), new Function<Cocktail, String>() {
            @Nullable @Override public String apply(@Nullable Cocktail cocktail) {
                return cocktail.getName();
            }
        });
        logger.trace("autocomplete word for {}:{}", term, words);
        return words;
    }

    @RequestMapping(value = "/cocktail/{id}", method = RequestMethod.PUT)
    public String update(@PathVariable String id, Cocktail cocktail, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/cocktail/{id}/edit";
        }

        cocktail.setId(id);

        // remove empty ingredients (caused by empty input fields in the GUI)
        Collection<Ingredient> ingredients = Collections2.filter(cocktail.getIngredients(), new Predicate<Ingredient>() {
            @Override
            public boolean apply(Ingredient ingredient) {
                return hasLength(ingredient.getName()) && hasLength(ingredient.getQuantity());
            }
        });

        cocktail.setIngredients(Lists.newArrayList(ingredients));
        cocktailRepository.save(cocktail);

        return "redirect:/cocktail/{id}";
    }

    /**
     * TODO use PUT instead of POST
     *
     * @param id    id of the cocktail
     * @param photo to associate with the cocktail
     * @return redirection to display cocktail
     */
    @RequestMapping(value = "/cocktail/{id}/photo", method = RequestMethod.POST)
    public String updatePhoto(@PathVariable String id, @RequestParam("photo") MultipartFile photo) {

        if (!photo.isEmpty()) {
            try {
                String contentType = fileStorageService.findContentType(photo.getOriginalFilename());
                if (contentType == null) {
                    logger.warn("photo", "Skip file with unsupported extension '" + photo.getOriginalFilename() + "'");
                } else {

                    InputStream photoInputStream = photo.getInputStream();
                    long photoSize = photo.getSize();

                    Map metadata = new TreeMap();
                    metadata.put("Content-Length",Arrays.asList(new String[] { ""+photoSize}));
                    metadata.put("Content-Type", Arrays.asList(new String[] { contentType}));
                    metadata.put("Cache-Control", Arrays.asList(new String[] {"public, max-age=" + TimeUnit.SECONDS.convert(365, TimeUnit.DAYS)}));
                    
                    

                /*    ObjectMetadata objectMetadata = new ObjectMetadata();
                    objectMetadata.setContentLength(photoSize);
                    objectMetadata.setContentType(contentType);
                    objectMetadata.setCacheControl("public, max-age=" + TimeUnit.SECONDS.convert(365, TimeUnit.DAYS));*/
                    String photoUrl = fileStorageService.storeFile(photo.getBytes(), metadata);

                    
                    Cocktail cocktail = cocktailRepository.findOne(id);
                    logger.info("Saved {}", photoUrl);
                    cocktail.setPhotoUrl(photoUrl);
                    cocktailRepository.save(cocktail);
                }

            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        }
        return "redirect:/cocktail/" + id;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/cocktail/{id}")
    public String view(@PathVariable String id, Model model) {
        Cocktail cocktail = cocktailRepository.findOne(id);
        if (cocktail == null) {
            throw new ResourceNotFoundException(id);
        }
        model.addAttribute(cocktail);
        return "cocktail/view";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/cocktail")
    public ModelAndView find(@RequestParam(value = "name", required = false) String name,
                             @RequestParam(value = "ingredient", required = false) String ingredient) {
        Collection<Cocktail> cocktails;
        if (name != null) {
            cocktails = cocktailRepository.findByName(name);
        }else if (ingredient != null){
            cocktails = cocktailRepository.findByIngredientsName(ingredient);
        } else {
            cocktails = Lists.newArrayList(cocktailRepository.findAll());
        }
        return new ModelAndView("cocktail/view-all", "cocktails", cocktails);
    }

    @ManagedMetric(metricType = MetricType.COUNTER)
    public int getAddedCommentCount() {
        return addedCommentCount.get();
    }
}
