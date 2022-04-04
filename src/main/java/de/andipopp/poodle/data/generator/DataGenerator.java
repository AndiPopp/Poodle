package de.andipopp.poodle.data.generator;

import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vaadin.flow.spring.annotation.SpringComponent;

import de.andipopp.poodle.data.Role;
import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.DateOption;
import de.andipopp.poodle.data.entity.polls.DatePoll;
import de.andipopp.poodle.data.service.OptionRepository;
import de.andipopp.poodle.data.service.PollRepository;
import de.andipopp.poodle.data.service.UserRepository;
import de.andipopp.poodle.data.service.VoteRepository;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(
    		PasswordEncoder passwordEncoder, 
    		UserRepository userRepository, 
    		PollRepository pollRepository,
    		OptionRepository optionRepository,
    		VoteRepository voteRepository
    ) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (userRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }

            logger.info("Generating demo data");

            logger.info("... generating 2 User entities...");
            User user = new User();
            user.setName("John Normal");
            user.setUsername("user");
            user.setHashedPassword(passwordEncoder.encode("user"));
            user.setProfilePictureUrl(
                    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            user.setRoles(Collections.singleton(Role.USER));
            userRepository.save(user);
            User admin = new User();
            admin.setName("Emma Powerful");
            admin.setUsername("admin");
            admin.setHashedPassword(passwordEncoder.encode("admin"));
            admin.setProfilePictureUrl(
                    "https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            admin.setRoles(Set.of(Role.USER, Role.ADMIN));
            userRepository.save(admin);

            //polls
            populatePollRepository(logger,pollRepository, optionRepository, voteRepository, user, admin);
            
            logger.info("Generated demo data");
        };
    }
    
    private void populatePollRepository(
    		Logger logger,
    		PollRepository pollRepository,
    		OptionRepository optionRepository,
    		VoteRepository voteRepository,
    		User user, 
    		User admin) {
    	DatePoll poll = new DatePoll();
    	poll.setTitle("Master of the Universe get-together");
    	poll.setDescription("He-Man will be there.");
    	poll.setLocation("Castle Greyskull (unless specified otherwise)");
    	poll.setOwner(user);
    	
    	poll.addOption(new DateOption(
				"With Skeletor", 
				new GregorianCalendar(2022, 2-1, 28, 9, 30).getTime(), 
				new GregorianCalendar(2022, 2-1, 28, 11, 0).getTime(), 
				"Snake Mountain"
			));
    	poll.addOption(new DateOption(
				new GregorianCalendar(2022, 2-1, 28, 15, 0).getTime(), 
				new GregorianCalendar(2022, 2-1, 28, 16, 30).getTime(), 
				"at Man-at-Arm's"
			));
    	poll.addOption(new DateOption(
    			new GregorianCalendar(2022, 4-1, 11, 8, 0).getTime(), 
    			new GregorianCalendar(2022, 4-1, 11, 10, 0).getTime()
    		));
    	poll.addOption(new DateOption(
    			new GregorianCalendar(2022, 4-1, 11, 15, 15).getTime(), 
    			new GregorianCalendar(2022, 4-1, 11, 16, 45).getTime()
    		));
    	poll.addEmptyVote();
    	logger.info(" Votes added: "+poll.getVotes().size());
    	pollRepository.save(poll);
    	
    	poll = new DatePoll();
    	poll.setTitle("Becoming Hokage");
    	poll.setLocation("Konohagekure");
    	poll.setOwner(user);
    	poll.addOption(new DateOption(
    			new GregorianCalendar(2022, 11-1, 23, 9, 10).getTime(), 
    			new GregorianCalendar(2022, 11-1, 23, 10, 40).getTime()
    		));
    	poll.addOption(new DateOption(
    			new GregorianCalendar(2022, 11-1, 23, 19, 10).getTime(), 
    			new GregorianCalendar(2022, 11-1, 23, 20, 40).getTime()
    		));
    	pollRepository.save(poll);
    	
    	poll = new DatePoll();
    	poll.setTitle("This is a really long title just to see what happens if we have a really long title and even longer titles");
    	poll.setDescription("Man this title is long");
    	poll.setOwner(user);
    	poll.addOption(new DateOption(
    			new GregorianCalendar(2022, 1-1, 1, 8, 0).getTime(), 
    			new GregorianCalendar(2022, 1-1, 1, 10, 0).getTime()
    		));
    	pollRepository.save(poll);
    	
    	poll = new DatePoll();
    	poll.setTitle("Only for admin's eyes");
    	poll.setDescription("Only the admin should see this");
    	poll.setOwner(admin);
    	poll.addOption(new DateOption(
    			new GregorianCalendar(2022, 1-1, 1, 8, 0).getTime(), 
    			new GregorianCalendar(2022, 1-1, 1, 10, 0).getTime()
    		));
    	pollRepository.save(poll);
    	
    	logger.info("Created " + pollRepository.count() + " example polls with " 
    			+ optionRepository.count() + " options and "
    			+ voteRepository.count() + " votes.");
    }

}