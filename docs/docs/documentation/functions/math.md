<!--
  ~ SPDX-FileCopyrightText: 2017-2024 Enedis
  ~
  ~ SPDX-License-Identifier: Apache-2.0
  ~
-->

!!! info "[Browse implementation](https://github.com/Enedis-OSS/chutney/blob/main/chutney/action-impl/src/main/java/com/chutneytesting/action/function/MathFunctions.java){:target="_blank"}"

Following functions help you write and shorten SpEL when you need to perform math operations.

## abs

!!! note "Integer abs(Integer a)"

    Returns the absolute value of a given integer.  
    ex. Integer -2 to 2.

    See [Math.abs()](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#abs-int-){:target="_blank"} for further details

    **Parameters** :

    * `Integer a` : The integer you want to get an absolute value
        * ex. -2

    **Returns** :

    * An absolute value of a given integer.

    **Examples** :

    SpEL without : `${T(java.lang.Math).abs(-2)}`

    SpEL with    : `${#abs(-2)}`

## min

!!! note "Integer min(Integer a, Integer b)"

    Returns the smaller value of a and b.  
    ex. Min of 4 and 9 is 4.


    See [Math.min()](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#min-int-int-){:target="_blank"} for further details

    **Parameters** :

    * `Integer a` : An integer
        * ex. 4
    * `Integer b` : Another integer
        * ex. 9

    **Returns** :

    * The smaller value of a and b.

    **Examples** :

    SpEL without : `${T(java.lang.Math).min(4, 9)}`

    SpEL with    : `${#min(4, 9)}`

## max

!!! note "Integer max(Integer a, Integer b)"

    Returns the greater value of a and b.  
    ex. Min of 4 and 9 is 9.

    See [Math.max()](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#max-int-int-){:target="_blank"} for further details

    **Parameters** :

    * `Integer a` : An integer
        * ex. 4
    * `Integer b` : Another integer
        * ex. 9

    **Returns** :

    * The greater value of a and b.

    **Examples** :

    SpEL without : `${T(java.lang.Math).max(4, 9)}`

    SpEL with    : `${#max(4, 9)}`
