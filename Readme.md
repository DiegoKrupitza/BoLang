![compact BoLang logo](./resources/compactlogo.png)

# Table of Contents

- [Table of Contents](#table-of-contents)
- [Introduction](#introduction)
- [Documentation](#documentation)
  * [Datatype](#datatype)
    + [Doubles](#doubles)
    + [Integers](#integers)
    + [Booleans](#booleans)
    + [Strings](#strings)
    + [Arrays](#arrays)
    + [Voids](#voids)
  * [Identifier](#identifier)
  * [External Parameters](#external-parameters)
  * [Operators](#operators)
    + [Negation](#negation)
      - [Integers, Doubles, Booleans](#integers--doubles--booleans)
      - [Strings](#strings-1)
      - [Arrays](#arrays-1)
    + [Addition](#addition)
    + [Subtraction](#subtraction)
    + [Division](#division)
    + [Multiplication](#multiplication)
    + [Concatenation](#concatenation)
    + [Equals](#equals)
    + [Not Equals](#not-equals)
    + [Greater Equals](#greater-equals)
    + [Greater](#greater)
    + [Less Equal](#less-equal)
    + [Less](#less)
    + [Logic And](#logic-and)
    + [Logic Or](#logic-or)
  * [Self defined functions](#self-defined-functions)
  * [Functions](#functions)

# Introduction

BoLang is a non turing complete programming language that is written in Java. With the flag `-f` you can allow the usage of self defined functions and make BoLang turing complete. The goal of this language is to have predefined functions that can be called by the user and do further operation on results etc.

# Documentation

Each program is written in a producral way, that means the first line is interpreted then the next line and so on. 

## Datatype

The programming language has 6 different datatypes:

* Doubles
* Integers
* Booleans
* Strings
* Arrays
* Voids

### Doubles

In BoLang a `double` represents a floating point number. Hardcoded digits that are a floating point will be automatically stored as a `double`.

```BoLang
return 5.5; // this will be return a double with the value 5.5
```

### Integers

As the name already suggests the Integer datatype represents integer values. Hardcoded integers in the program are automatically stored as an `integer`. The range of an integer is from -2147483648 to 2147483647.

```BoLang
return 5; // this will be return an integer with the value 5
```

### Booleans

In BoLang you can also use `true` or `false` to represent Boolean values. There is no difference between `true` and `TRUE` or `false` and `FALSE`. You can use both as you like.

```BoLang
return true; // returns a boolean with the value true
return TRUE; // this is equal to the line above
```

```BoLang
return false; // returns a boolean with the value false
return FALSE; // this is equal to the line above
```

### Strings

To represent Strings in BoLang you have to use the double quotation mark to open the string and also to close the string.

```BoLang
return "Hello BoLang!"; // returns a string with the value "Hello BoLang!"
```

You can also access a single char from the string by using the index access operation.

```
var x := "Hey!"; // creates a string and stores it into the variable with the name x
return x[0]; // returns the character `H`
```

### Arrays

Bundling values can be done in BoLang with Arrays. Arrays can contain elements of any type.

```BoLang
return [1,2,"three",4.4,false]; // returns an array with 5 values of type [Integer, Integer, String, Double, Boolean] 
```

Its important that after initialization arrays can not be changed. You can only access the elements by their index (which starts at 0).

```BoLang
var x := [1,2,"three",4.4,false]; // initialization of an array with 5 values of type [Integer, Integer, String, Double, Boolean] 
return x[0] + x[1]; // access the values from array x at index 0 and 1 and performs an addition on both values
```

### Voids

Voids are only present for functions that do not return any value. Voids cannot be returned or assigned to variables. 

## Identifier

In BoLang you can define your own variables and use them. To define a variable you only need to choose a name and a given default value. Its important to highlight that the name has to match the following regex pattern `[_A-Za-z] [_0-9A-Za-z]*`

```BoLang
var x := "Hey this is the first var"; 
var y := 4;
var booleanVar := false;
```

 ## External Parameters

Each BoLang program can be interpreted with a list of parameters. Those parameters can be accessed by name. Its important to highlight that params are always interpreted as Strings. So if you want to use the value of a parameter as an Integer/Number/... you have to call a function such as `toInt` etc.

For example we call execute the program with a parameter called `number` that contains the value `"5"`.

```BoLang
return #number; // returns the value of the parameter `number` as a String
```

## Operators

### Negation

#### Integers, Doubles, Booleans

The semantics of the negation operator depends on the type of element we perform the operation. On doubles and integers the result of a negation is simply the value mulitplied by `-1`. On booleans the value is flipped, meaning `true` becomes `false` and the otherway arround.

```BoLang
return !1; // returns the Integer -1
return !1.1; // returns the Integer -1.1
return !false; // return the boolean true
```

#### Strings

The only special meaning of the negation operation comes in hand when taking a look at negating a String. Negating a string will flip all the character in the string in the ascii table. Means the ascii character at index 0 becomes 127, the ascii character at index 1 becomes the 126 and so on.

```BoLang
return !"B"; // returns the value "="
return !"Z"; // returns the value "%"
```

Keep in mind that not all characters of the ascii table can be displayed.

#### Arrays

You can also negate arrays in BoLang. The semantics here mean that you perform the negation on all the elements of the array. The returned value will be an array again.

```BoLang
return ![1,2,"B","Z",false]; // returns an array with the values [-1,-2,"=","%",true]
```

### Addition

This operation only works on `doubles`, `integers` and `arrays`. On doubles and integers this operation performs as the name suggests an addition. The result type depends on the dominance of the inputs. If one of the operants is a `double` the result is a `double` again.

```BoLang
return 1 + 1; // returns 2 type of Integer
return 1.0 + 1; // returns 2.0 type of Double
return 1.0 + 1.0; // returns 2.0 type of Double
```

For `arrays` its a bit more complicated. If *only* one operant is of type `array` the array recursivly performs an addition on each element of the array with the other operant that is not an array.

```BoLang
return [1,2,3] + 1; // returns an array [2,3,4]
```

If both operants are an array: element 1 of array *a* is added to the element 1 of array *b* and so on. If one array is bigger than the odder the remaining elements get appended to the returned array.

```BoLang
return [1,2,3] + [2,2,2]; // returns an array [3,4,5]
return [1,2,3] + [1,2]; // return an array [2,4,3]
```

### Subtraction

This operation only works on `doubles`, `integers` and `arrays`. On doubles and integers this operation performs as the name suggests an subtraction. The result type depends on the dominance of the inputs. If one of the operants is a `double` the result is a `double` again.

```BoLang
return 3 - 1; // returns 2 type of Integer
return 3.0 - 1; // returns 2.0 type of Double
return 3.0 - 1.0; // returns 2.0 type of Double
```

For `arrays` its a bit more complicated. If *only* one operant is of type `array` the array recursivly performs an subtraction on each element of the array with the other operant that is not an array.

```BoLang
return [1,2,3] - 1; // returns an array [0,1,2]
```

If both operants are an array: element 1 of array *a* is subtracted by the element 1 of array *b* and so on. If the left array is bigger than the left the remaining elements are just appended. If the right array is bigger than the left the remaining elements are substracted with 0. 

```BoLang
return [1,2,3] - [1,2]; // returns an array [0,0,3]
return [1,2] + [1,2,3]; // return an array [0,0,-3]
```

### Division

This operation only works on `doubles`, `integers` and `arrays`. On doubles and integers this operation performs as the name suggests an division. The result type depends on the dominance of the inputs. If one of the operants is a `double` the result is a `double` again. If both operants are of type `integer` and the division can be performed without a remaining the result is of type `integer` otherwise it is of type `double`.

```BoLang
return 3 / 1; // returns 3 type of Integer
return 1 / 2; // returns 0.5 type of Double
return 3.0 / 1; // returns 1.0 type of Double
return 3.0 / 1.0; // returns 1.0 type of Double
```

For `arrays` its a bit more complicated. If *only* one operant is of type `array` the array recursivly performs an division on each element of the array with the other operant that is not an array.

```BoLang
return [1,2,3] / 1; // returns an array [1,2,3]
```

If both operants are an array: element 1 of array *a* is divided by the element 1 of array *b* and so on. If the left array is bigger than the left the remaining elements are just appended. If the right array is bigger than the left the remaining elements are divided by 0. 

```BoLang
return [1,2,3] / [1,2]; // returns an array [1,1,3]
return [1,2] / [1,2,3]; // return an array [1,1,0]
```

### Multiplication 

This operation only works on `doubles`, `integers` and `arrays`. On doubles and integers this operation performs as the name suggests an multiplication. The result type depends on the dominance of the inputs. If one of the operants is a `double` the result is a `double` again.

```BoLang
return 3 * 2; // returns 6 type of Integer
return 3.0 * 2; // returns 6.0 type of Double
return 3.0 * 2.0; // returns 6.0 type of Double
```

For `arrays` its a bit more complicated. If *only* one operant is of type `array` the array recursivly performs an multiplication on each element of the array with the other operant that is not an array.

```BoLang
return [2,4,6] * 2; // returns an array [4,8,12]
```

If both operants are an array: element 1 of array *a* is multiplied by the element 1 of array *b* and so on. If one array is bigger the remaining elements are appended to the result array

```BoLang
return [1,2,3] * [1,2]; // returns an array [1,4,3]
return [1,2] / [1,2,3]; // return an array [1,4,3]
return [] / [1,2,3]; // return an array [1,2,3]
```

### Concatenation

This works on any datatype. It simple concatenates the value of the left operant to the value of the right operant. The result type is always a string.

```BoLang
return "Hey" ++ 1; // returns the string "Hey1"
return "1" ++ 1; // returns the string "11"
return true ++ 1; // returns the string "true1"
```

### Equals

The equality operator works on the type and the value. For equality there is not difference between the type of `doubles` and `integers`. For all the other ones the types are important for the equality check.

```BoLang
return 5.5 == 5; // not equals
return 5.0 == 5; // equals
return "5" == 5; // not equals
return "BoLang!" == "BoLang!"; // equals
return true == true; // equals
```

The result type of this operator is always a `boolean`.

### Not Equals

It is basically the inverted value of the equals operator.

```BoLang
return 5.5 != 5; // true
return 5.0 != 5; // false
return "5" != 5; // true
return "BoLang!" != "BoLang!"; // false
return true != true; // false
```

The result type of this operator is always a `boolean`.

### Greater Equals

The greater equals operation only works on `doubles` and `integers`. The semantics of this operation is equivalent to the mathematic one. 

```BoLang
return 6 >= 5; // true
return 5 >= 5; // true
return 5.0 >= 5; // true
return 4 >= 5; // false
```

The result type of this operator is always a `boolean`.

### Greater

The greater operation only works on `doubles` and `integers`. The semantics of this operation is equivalent to the mathematic one. 

```BoLang
return 6 > 5; // true
return 5 > 5; // false
return 5.0 > 5; // false
return 4 > 5; // false
```

The result type of this operator is always a `boolean`.

### Less Equal

The less equal operation only works on `doubles` and `integers`. The semantics of this operation is equivalent to the mathematic one. 

```BoLang
return 6 <= 5; // false
return 5 <= 5; // true
return 5.0 <= 5; // true
return 4 <= 5; // true
```

The result type of this operator is always a `boolean`.

### Less 

The less operation only works on `doubles` and `integers`. The semantics of this operation is equivalent to the mathematic one. 

```BoLang
return 6 < 5; // true
return 5 < 5; // false
return 5.0 < 5; // false
return 4 < 5; // true
```

The result type of this operator is always a `boolean`.

### Logic And

The `logic and` operation symboled by the characters `&&` only works on `booleans`. The operation only works on `booleans`.

```BoLang
return false && false; // false
return true && false; // false
return false && true; // false
return true && true; // true
```

The result type of this operator is always a `boolean`.

### Logic Or

The `logic or` operation symboled by the characters `||` only works on `booleans`. The operation only works on `booleans`.

```BoLang
return false || false; // false
return true || false; // true
return false || true; // true
return true || true; // true
```

The result type of this operator is always a `boolean`.

## Self defined functions

By default BoLang does not support self defined functions but with the flag `-f` you can allow them. Self defined functions can be defined in any section of the program means you can defined it before or after usage. Self defined functions are in by default in the module `this`. So calling a self defined function will be in the form of `this.[functionName]`. 

```BoLang
function add(a,b) {
	return a + b;
}

return 10 - this.add(2,3); // returns 5
```


## Functions

| Module | Name     | Parameters   | Description | Result type | Example |
| ------ | -------- | ------------ | ----------- | ------- | -------- |
| Random | `rand()` | No parameter | Generates a random double between (inclusive) 0 and (exclusive) 1 | `double` | `Random.rand()` |
| Random | `rand(upperBound)` | `upperBound` has to be a `double` or a `integer` | Generates a random double (inclusive) 0 and (exclusive) the provided `upperBound` | `double` | `Random.rand(100)` |
| Random | `rand(lowerBound, upperBound)` | `upperBound` and `lowerBound` has to be a `double` or a `integer`. The parameter `lowerBound` has to be smaller than `upperBound` | Generates a random double (inclusive) `lowerBound` and (exclusive) `upperBound` | `double` | `Random.rand(5,10)` |
| Random | `randInt()` | No parameter | Generates a random integer between (inclusive) 0 and (exclusive) 1 | `integer` | `Random.randInt()` |
| Random | `randInt(upperBound)` | `upperBound` has to be a `double` or a `integer` | Generates a random integer (inclusive) 0 and (exclusive) the provided `upperBound` | `integer` | `Random.randInt(100)` |
| Random | `randInt(lowerBound, upperBound)` | `upperBound` and `lowerBound` has to be a `double` or a `integer`. The parameter `lowerBound` has to be smaller than `upperBound` | Generates a random integer (inclusive) `lowerBound` and (exclusive) `upperBound` | `integer` | `Random.randInt(5,10)` |

