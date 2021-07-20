![compact BoLang logo](./resources/compactlogo.png)

# Introduction

BoLang is a currently non turing complete programming language that is written in Java. The goal of this language is to have predefined functions that can be called by the user and do further operation on results etc.

# Documentation

Each program is written in a producral way, that means the first line is interpreted then the next line and so on. 

## Datatype

The programming language has 5 different datatypes:

* Numbers
* Integers
* Booleans
* Strings
* Arrays

### Numbers

In BoLang a `number` represents a floating point number. Hardcoded digits that are a floating point will be automatically stored as a `number`.

```BoLang
return 5.5; // this will be return a number with the value 5.5
```

### Integers

As the name already suggests the Integer datatype represents integer values. Hardcoded integers in the program are automatically stored as an `integer`

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
return [1,2,"three",4.4,false]; // returns an array with 5 values of type [Integer, Integer, String, Number, Boolean] 
```

Its important that after initialization arrays can not be changed. You can only access the elements by their index (which starts at 0).

```BoLang
var x := [1,2,"three",4.4,false]; // initialization of an array with 5 values of type [Integer, Integer, String, Number, Boolean] 
return x[0] + x[1]; // access the values from array x at index 0 and 1 and performs an addition on both values
```

### Identifier

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
return #number; // returns the value of the parameter `number`
```

## Operators

### Negation

#### Integers, Numbers, Booleans

The semantics of the negation operator depends on the type of element we perform the operation. On numbers and integers the result of a negation is simply the value mulitplied by `-1`. On booleans the value is flipped, meaning `true` becomes `false` and the otherway arround.

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

