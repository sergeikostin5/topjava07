package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GKislin
 * 31.05.2015.
 */
public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,10,0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,13,0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,20,0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,10,0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,13,0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,20,0), "Ужин", 510)
        );
        List<UserMealWithExceed> filteredMealsWithExceeded = getFilteredWithExceededByCycle(mealList, LocalTime.of(7, 0), LocalTime.of(23,0), 2000);
        filteredMealsWithExceeded.forEach(m -> System.out.println(m));
    }

    public static List<UserMealWithExceed>  getFilteredWithExceeded(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumByDate = mealList.stream().collect(Collectors.groupingBy(userMeal -> userMeal.getDateTime().toLocalDate(),
                                  Collectors.summingInt(userMeal -> userMeal.getCalories())));

        return mealList.stream().filter(userMeal -> TimeUtil.isBetween(userMeal.getDateTime().toLocalTime(), startTime, endTime)).
                map(userMeal -> new UserMealWithExceed(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(),
                        caloriesSumByDate.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay)).
                            collect(Collectors.toList());
    }

    public static List<UserMealWithExceed>  getFilteredWithExceededByCycle(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumByDate = new HashMap<>();
        for(UserMeal userMeal : mealList){
            LocalDate mealDate = userMeal.getDateTime().toLocalDate();
            caloriesSumByDate.put(mealDate, caloriesSumByDate.getOrDefault(mealDate, 0) + userMeal.getCalories());
        }

        List<UserMealWithExceed> mealExceeded = new ArrayList<>();
        for(UserMeal userMeal : mealList){
            LocalDateTime dateTime = userMeal.getDateTime();
            if(TimeUtil.isBetween(dateTime.toLocalTime(), startTime, endTime)){
                mealExceeded.add(new UserMealWithExceed(dateTime, userMeal.getDescription(), userMeal.getCalories(), caloriesSumByDate.get(dateTime.toLocalDate()) > caloriesPerDay));
            }
        }

        return mealExceeded;

    }
}
