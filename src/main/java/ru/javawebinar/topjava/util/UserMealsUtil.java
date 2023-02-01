package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with excess. Implement by cycles
        boolean exceed;
        List<UserMealWithExcess> returnUsers = new ArrayList<>();

        for(UserMeal user: meals){
            exceed = isExcess(meals,caloriesPerDay).get(user.getDateTime().getDayOfMonth()) > caloriesPerDay;

            if (TimeUtil.isBetweenHalfOpen(
                    LocalTime.of(user.getDateTime().getHour(), user.getDateTime().getMinute()), startTime, endTime)) {
                returnUsers.add(new UserMealWithExcess(user.getDateTime(),user.getDescription(),caloriesPerDay,exceed));
            }
        }
        return returnUsers;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO Implement by streams
        List<UserMealWithExcess> filteredMeals = new ArrayList<>();
        Map<LocalDate, Integer> map = new TreeMap<>();

        meals.forEach(meal -> map.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum));
        meals.forEach(meal -> {
            if (TimeUtil.isBetweenHalfOpen(
                    LocalTime.of(meal.getDateTime().getHour(), meal.getDateTime().getMinute()), startTime, endTime)
            ) {
                filteredMeals.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), caloriesPerDay,
                        excess(map.get(meal.getDateTime().toLocalDate()), caloriesPerDay)));
            }
        });
        return filteredMeals;
    }
    public static Map<Integer, Integer> isExcess(List<UserMeal> meals, int caloriesPerDay){
        Map<Integer, Integer> hashmap = new HashMap<>();
        for(UserMeal user: meals){
            if(!hashmap.containsKey(user.getDateTime().getDayOfMonth())) hashmap.put(user.getDateTime().getDayOfMonth(), user.getCalories());
            else hashmap.put(user.getDateTime().getDayOfMonth(), hashmap.get(user.getDateTime().getDayOfMonth())+user.getCalories());
        }
        return hashmap;
    }
    public static boolean excess(int realCaloriesPerDay, int caloriesPerDay) {
        return realCaloriesPerDay > caloriesPerDay;
    }
}
