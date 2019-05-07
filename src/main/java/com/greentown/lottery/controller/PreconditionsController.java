package com.greentown.lottery.controller;

import com.google.common.base.Preconditions;

/**
 * @author jairy
 * @date 2019/5/5
 */
public class PreconditionsController {

    public static void main(String[] args) {
        Integer number = 10;

        Preconditions.checkNotNull(number, "Illegal Argument passed: First parameter is Null.");
        Preconditions.checkArgument(number > 0.0, "Illegal Argument passed: Negative value %s.", number);

        Preconditions.checkElementIndex(number,10,"Illegal Argument passed: Invalid index.");

        Preconditions.checkPositionIndex(number,10,"Illegal Argument passed: Invalid index.");


    }
}
