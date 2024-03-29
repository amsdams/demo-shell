package com.example.demo.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.AbsoluteWidthSizeConstraints;
import org.springframework.shell.table.BeanListTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.CellMatchers;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;
import org.springframework.util.StringUtils;

import com.example.demo.model.CliUser;
import com.example.demo.model.Gender;
import com.example.demo.service.UserService;
import com.example.demo.shell.InputReader;
import com.example.demo.shell.ShellHelper;
import com.example.demo.shell.table.BeanTableModelBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

@ShellComponent
public class UserCommand {

    private static final String GENDER = "Gender";

	private static final String FULL_NAME = "Full name";

	@Autowired
    ShellHelper shellHelper;

    @Autowired
    InputReader inputReader;

    @Autowired
    UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @ShellMethod("Display list of users")
    public void userList() {
        List<CliUser> users = userService.findAll();

        LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        headers.put("id", "Id");
        headers.put("username", "Username");
        headers.put("fullName", FULL_NAME);
        headers.put("gender", GENDER);
        headers.put("superuser", "Superuser");
        TableModel model = new BeanListTableModel<>(users, headers);

        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addInnerBorder(BorderStyle.fancy_light);
        tableBuilder.addHeaderBorder(BorderStyle.fancy_double);
        shellHelper.print(tableBuilder.build().render(80));
    }

    @ShellMethod("Create new user with supplied username")
    public void createUser(@ShellOption({"-U", "--username"}) String username) {
        if (userService.exists(username)) {
            shellHelper.printError(String.format("User with username='%s' already exists --> ABORTING", username));
            return;
        }
        CliUser user = new CliUser();
        user.setUsername(username);

        shellHelper.printInfo("Please enter new user data:");
        // 1. read user's fullName --------------------------------------------
        do {
            String fullName = inputReader.prompt(FULL_NAME);
            if (StringUtils.hasText(fullName)) {
                user.setFullName(fullName);
            } else {
                shellHelper.printWarning("User's full name CAN NOT be empty string? Please enter valid value!");
            }
        } while (user.getFullName() == null);

        // 2. read user's password --------------------------------------------
        do {
            String password = inputReader.prompt("Password", "secret", false);
            if (StringUtils.hasText(password)) {
                user.setPassword(password);
            } else {
                shellHelper.printWarning("Password'CAN NOT be empty string? Please enter valid value!");
            }
        } while (user.getPassword() == null);

        // 3. Prompt for user's Gender ----------------------------------------------
        Map<String, String> options = new HashMap<>();
        options.put("M", Gender.MALE.name());
        options.put("F", Gender.FEMALE.name() );
        options.put("D", Gender.DIVERSE.name());

        String genderValue = inputReader.selectFromList(GENDER, "Please enter one of the [] values", options, true, null);
        Gender gender = Gender.valueOf(options.get(genderValue.toUpperCase()));
        user.setGender(gender);

        // 4. Prompt for superuser attribute
        String superuserValue = inputReader.promptWithOptions("New user is superuser", "N", Arrays.asList("Y", "N"));
        if ("Y".equals(superuserValue)) {
            user.setSuperuser(true);
        } else {
            user.setSuperuser(false);
        }

        // Print user's input -------------------------------------------------
        shellHelper.printInfo("\nCreating a new user:");
        displayUser(user);

        CliUser createdUser = userService.create(user);
        shellHelper.printSuccess("---> SUCCESS created user with id=" + createdUser.getId());
    }

    @ShellMethod("Update and synchronize all users in local database with external source")
    public void updateAllUsers() {
        shellHelper.printInfo("Starting local user db update");
        long numOfUsers = userService.updateAll();
        String successMessage = shellHelper.getSuccessMessage("SUCCESS >>");
        successMessage = successMessage + String.format(" Total of %d local db users updated!", numOfUsers);
        shellHelper.print(successMessage);
    }

    @ShellMethod("Display details of user with supplied username")
    public void userDetails(@ShellOption({"-U", "--username"}) String username) {
        CliUser user = userService.findByUsername(username);
        if (user == null) {
            shellHelper.printWarning("No user with the supplied username could be found?!");
            return;
        }
        displayUser(user);
    }

    private void displayUser(CliUser user) {
        LinkedHashMap<String, Object> labels = new LinkedHashMap<>();
        labels.put("id", "Id");
        labels.put("username", "Username");
        labels.put("fullName", FULL_NAME);
        labels.put("gender", GENDER);
        labels.put("superuser", "Superuser");
        labels.put("password", "Password");

        String[] header = new String[] {"Property", "Value"};
        BeanTableModelBuilder builder = new BeanTableModelBuilder(user, objectMapper);
        TableModel model = builder.withLabels(labels).withHeader(header).build();

        TableBuilder tableBuilder = new TableBuilder(model);

        tableBuilder.addInnerBorder(BorderStyle.fancy_light);
        tableBuilder.addHeaderBorder(BorderStyle.fancy_double);
        tableBuilder.on(CellMatchers.column(0)).addSizer(new AbsoluteWidthSizeConstraints(20));
        tableBuilder.on(CellMatchers.column(1)).addSizer(new AbsoluteWidthSizeConstraints(30));
        shellHelper.print(tableBuilder.build().render(80));
    }

}
