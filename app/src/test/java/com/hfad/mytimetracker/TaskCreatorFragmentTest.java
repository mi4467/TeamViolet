package com.hfad.mytimetracker;

import org.apache.tools.ant.Task;
import org.junit.Test;

import static org.junit.Assert.*;

public class TaskCreatorFragmentTest {


    @Test
    public void checkValidityReoccTaskTest(){
        TaskCreatorFragment test = new TaskCreatorFragment();
        String name = null;
        TaskCreatorFragment.setReoccTaskName(name);
        assertFalse(test.checkValidityOfReoccTask());
        Integer endyear = null;
        TaskCreatorFragment.setEndYear(endyear);
        assertFalse(test.checkValidityOfReoccTask());
        Integer endmonth = null;
        TaskCreatorFragment.setEndMonth(endmonth);
        assertFalse(test.checkValidityOfReoccTask());
        Integer endday = null;
        TaskCreatorFragment.setEndDay(endday);
        assertFalse(test.checkValidityOfReoccTask());
        Integer startYear = null;
        TaskCreatorFragment.setStartYear(startYear);
        assertFalse(test.checkValidityOfReoccTask());
        Integer startMonth = null;
        TaskCreatorFragment.setStartMonth(startMonth);
        assertFalse(test.checkValidityOfReoccTask());
        Integer startDay = null;
        TaskCreatorFragment.setStartDate(startDay);
        assertFalse(test.checkValidityOfReoccTask());
        Integer startHour = null;
        TaskCreatorFragment.setStartHourReocc(startHour);
        assertFalse(test.checkValidityOfReoccTask());
        Integer startMinute = null;
        TaskCreatorFragment.setStartMinuteReocc(startMinute);
        assertFalse(test.checkValidityOfReoccTask());
        Integer endHour = null;
        TaskCreatorFragment.setEndHourReocc(endHour);
        assertFalse(test.checkValidityOfReoccTask());
        Integer endMinute = null;
        TaskCreatorFragment.setEndMinuteReocc(endMinute);
        assertFalse(test.checkValidityOfReoccTask());
        TaskCreatorFragment.setCategoriesReocc(null);
        assertFalse(test.checkValidityOfReoccTask());
        TaskCreatorFragment.setDays(null);
        assertFalse(test.checkValidityOfReoccTask());
        //test null stuff

        name = "has name";
        TaskCreatorFragment.setReoccTaskName(name);
        assertFalse(test.checkValidityOfReoccTask());
        TaskCreatorFragment.setEndYear(2019);
        assertFalse(test.checkValidityOfReoccTask());
        TaskCreatorFragment.setEndMonth(11);
        assertFalse(test.checkValidityOfReoccTask());
        TaskCreatorFragment.setEndDay(12);
        assertFalse(test.checkValidityOfReoccTask());
        TaskCreatorFragment.setStartYear(2019);
        assertFalse(test.checkValidityOfReoccTask());
        TaskCreatorFragment.setStartMonth(11);
        assertFalse(test.checkValidityOfReoccTask());
        TaskCreatorFragment.setStartDate(12);
        assertFalse(test.checkValidityOfReoccTask());
        TaskCreatorFragment.setStartHourReocc(12);
        assertFalse(test.checkValidityOfReoccTask());
        TaskCreatorFragment.setEndMinuteReocc(20);
        assertFalse(test.checkValidityOfReoccTask());
        TaskCreatorFragment.setEndMinuteReocc(11);
        assertFalse(test.checkValidityOfReoccTask());
        TaskCreatorFragment.setEndMinute(12);
        assertFalse(test.checkValidityOfReoccTask());
        TaskCreatorFragment.setCategoriesReocc(new String[] {"Pizza"});
        assertFalse(test.checkValidityOfReoccTask());
        //tests to see if chain of nulls work

        name = "has name";
        TaskCreatorFragment.setReoccTaskName(name);
        TaskCreatorFragment.setEndYear(2019);
        TaskCreatorFragment.setEndMonth(11);
        TaskCreatorFragment.setEndDay(12);
        TaskCreatorFragment.setStartYear(2020);
        TaskCreatorFragment.setStartMonth(11);
        TaskCreatorFragment.setStartDate(12);
        TaskCreatorFragment.setStartHourReocc(7);
        TaskCreatorFragment.setEndMinuteReocc(20);
        TaskCreatorFragment.setEndMinuteReocc(8);
        TaskCreatorFragment.setEndMinute(12);
        TaskCreatorFragment.setCategoriesReocc(new String[] {"Pizza"});
        assertFalse(test.checkValidityOfReoccTask());
        //test that start year after end year is invalid

        name = "has name";
        TaskCreatorFragment.setReoccTaskName(name);
        TaskCreatorFragment.setEndYear(2018);
        TaskCreatorFragment.setEndMonth(11);
        TaskCreatorFragment.setEndDay(12);
        TaskCreatorFragment.setStartYear(2018);
        TaskCreatorFragment.setStartMonth(12);
        TaskCreatorFragment.setStartDate(12);
        TaskCreatorFragment.setStartHourReocc(7);
        TaskCreatorFragment.setEndMinuteReocc(20);
        TaskCreatorFragment.setEndMinuteReocc(8);
        TaskCreatorFragment.setEndMinute(12);
        TaskCreatorFragment.setCategoriesReocc(new String[] {"Pizza"});
        assertFalse(test.checkValidityOfReoccTask());
        //test that start month after end month is invalid

        name = "has name";
        TaskCreatorFragment.setReoccTaskName(name);
        TaskCreatorFragment.setEndYear(2019);
        TaskCreatorFragment.setEndMonth(11);
        TaskCreatorFragment.setEndDay(12);
        TaskCreatorFragment.setStartYear(2019);
        TaskCreatorFragment.setStartMonth(11);
        TaskCreatorFragment.setStartDate(16);
        TaskCreatorFragment.setStartHourReocc(7);
        TaskCreatorFragment.setEndMinuteReocc(20);
        TaskCreatorFragment.setEndMinuteReocc(8);
        TaskCreatorFragment.setEndMinute(12);
        TaskCreatorFragment.setCategoriesReocc(new String[] {"Pizza"});
        assertFalse(test.checkValidityOfReoccTask());
        //test that start day after end day is invalid

        name = "has name";
        TaskCreatorFragment.setReoccTaskName(name);
        TaskCreatorFragment.setEndYear(2019);
        TaskCreatorFragment.setEndMonth(11);
        TaskCreatorFragment.setEndDay(12);
        TaskCreatorFragment.setStartYear(2019);
        TaskCreatorFragment.setStartMonth(11);
        TaskCreatorFragment.setStartDate(12);
        TaskCreatorFragment.setStartHourReocc(23);
        TaskCreatorFragment.setStartMinuteReocc(12);
        TaskCreatorFragment.setEndHourReocc(20);
        TaskCreatorFragment.setEndMinuteReocc(8);
        TaskCreatorFragment.setCategoriesReocc(new String[] {"Pizza"});
        assertFalse(test.checkValidityOfReoccTask());
        //test that start hour after end hour is invalid

        name = "has name";
        TaskCreatorFragment.setReoccTaskName(name);
        TaskCreatorFragment.setEndYear(2019);
        TaskCreatorFragment.setEndMonth(11);
        TaskCreatorFragment.setEndDay(12);
        TaskCreatorFragment.setStartYear(2019);
        TaskCreatorFragment.setStartMonth(11);
        TaskCreatorFragment.setStartDate(12);
        TaskCreatorFragment.setStartHourReocc(23);
        TaskCreatorFragment.setStartMinuteReocc(12);
        TaskCreatorFragment.setEndHourReocc(23);
        TaskCreatorFragment.setEndMinuteReocc(15);
        TaskCreatorFragment.setCategoriesReocc(new String[] {"Pizza"});
        assertTrue(test.checkValidityOfReoccTask());
        //test that valid within



    }

    @Test
    public void checkValidityTaskTest(){

    }

    @Test
    public void cleanCardViewTwoTest(){

    }

    @Test
    public void cleanCardViewThreeTest(){

    }

}