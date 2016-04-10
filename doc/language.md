Language
========

Types
-----

**Null**

The `null` value which is also used in place of `undefined`.

**Boolean**

A value that can be `true` or `false`.

**Number**

A double precision floating point number like `10.5`.

**String**

A string value like `'a'` or `"a"`.

**Object**

An object like `{ a: 1, b: 2 }`. Objects can have properties which are accessed like `obj.x = 2` and can use
another object as its prototype by setting the prototype property. For example:

	objectProto = { b: 2 };
	obj = { a: 1 };
	obj.prototype = objectProto;
	print(obj.b); // => 2

**Array**

An array like `[1, 2, 3]`. Array elements are accessed using square brackets like `a[0] = 1`.

Arrays can also act as objects and contain properties.

**Function**

Functions can be created as anonymous functions with `function() {}` or the arrow notation like `(x) => {}` or `x => x + 1`, or
declared like `function f() {}`.

Functions have access to a `this` value. Arrow functions use the value of `this` from the enclosing context. Other functions
use the object that the caller called the function on, or `null` if there is none. Functions also have access to an `arguments`
array that contains all the arguments to the function.

Functions can also act as objects and contain properties.

Operators
---------

`++` `--` `void` `+` `!` `-` `*` `/` `%` `+` `-`

`~` `&` `|` `^` `<<` `>>>` `>>`

`<=` `>=` `<` `>` `==` `!=`

`&&` `||` `? :`

`=` `*=` `/=` `%=` `+=` `-=` `<<=` `>>=` `>>>=` `&=` `^=` `|=`

These operators follow the same rules for predecence, associativity, and short-circuiting as JavaScript but
there is no type conversion. The `+` operator is overloaded for numeric addition and string concatenation. The `<=`,
`>=`, `<`, and `>` operators are overloaded for numberic comparison and string comparison as long as both operands
are the same type.

Statements
----------

**Expression Statement**

An expression followed by a semicolon.

**Block**

Any number of statements between `{` and `}`

**Variable Declaration**

`var x;`

`var x = 0;`

`var x = 1, y = 2;`

**Function Declaration**

`function f(x) { return x + 1; }`
	
**If Statements**

`if(condition) { ... }`

`if(condition) { ... } else { ... } `

`if(condition) { ... } else if(condition) { ... } `

`if(condition) { ... } else if(condition) { ... } else(condition) { ... }`

**Iteration Statements**

`do { ... } while(condition)`

`while(condition) { ... }`

`for(initializer; condition; increment) { ... }`

`for(var ..., condition; increment) { ... }`

Visual Objects
==============

Visual objects are declared using special statements beginning with the `@` symbol. These objects have a comma separated list of properties which can be defined
in terms of variables and functions within the program. When these values are changed in the program, then the objects are updated automatically, and the graphical
view is redrawn. Except for `@vizUpdatesOff` and `@vizUpdatesOn`, objects are scoped to the current function. When the function ends, then the object is removed.

**Filters**

Existing objects can be filters and modified like:

	@rect[x:50](x:100);
	
which would change all the objects with an `x` property equal to 50 to have an `x` property of 100.

**Control Objects**

**@vizUpdatesOff** - disables visual updates.

**@vizUpdatesOn** - enables visual updates.

**@delay(...)**

- **instruction**: the number of milliseconds to wait between instructions when running the program.
- **transition**: the number of mulliseconds that an object transition will last for.

**@bounds()**

- **fit**: changes the bounds of the graphics canvas. 'none' for no fitting, 'extends' to increase the bounds of the canvas as necessary, 'full' to fully fit the
bounds of the canvas to the displayed objects.

**Iteration Objects**

**@for(...)** 

Creates multiple objects by iterating over an array. For example:

	@for(x <- array) @rect(x: x);
	@for(x <- array) { @rect(x: x, y: 0); @rect(x: x, y: 50); }

Can filter items with conditions:

	@for(x <- array2, x > 20) @rect(x: x);

Can iterate over multiple arrays with comma separated items:

	@for(x <- array2, y <- array2) @rect(x: x, y: y);

Objects are identified using the statement that created it along with the set of values the loop variables had. If the identity of an object is the same but its properties have
changed, then the transition will be animated in the graphical viewer.

**Common Properties**

- **fill**: the color the object is filled with like '#f00', '#ff0000', or one of: 'none', 'red', 'green', 'blue', 'yellow', 'magenta', 'cyan', 'white', 'lightgrey', 'grey', 'darkgrey', 'black'.
- **stroke**: the color of the stroke around the object.
- **strokeWidth**: the width of the stroke around the object.
- **strokeStyle**: the style of the stroke around the object as one of: 'solid', 'dot', 'dash', 'dashdot', 'dashdotdot'.

**@rect(...)**

- **x**: leftmost point on the rectangle.
- **y**: topmost point on the rectangle.
- **width**: width of the rectangle.
- **height**: height of the rectangle.
- **rx**: x radius of corners for rounded rectangles.
- **ry**: y radius of corners for rounded rectangles.

**@ellipse(...)**

- **cx**: center horizontal point of the ellipse.
- **cy**: center vertical point of the ellipse.
- **rx**: the horizontal radius of the ellipse.
- **ry**: the vertical radius of the ellipse.

**@circle(...)**

- **cx**: center horizontal point of the circle.
- **cy**: center vertical point of the circle.
- **r**: the radius of the circle.

**@line(...)**

- **x1**: the horizontal position of the starting point of the line.
- **y1**: the vertical position of the starting point of the line.
- **x2**: the horizontal position of the ending point of the line.
- **y2**: the vertical position of the ending point of the line.
- **arrowLength**: the length of the arrow at the end of the line.
- **arrowAngle**: the angle of the arrow heads at the end of the line.
- **startOffset**: the position from the start of the line to start drawing the line.
- **endOffset**: the position from the end of the line to stop drawing the line.

**@text(...)**

- **x**: the horizontal position of the text.
- **y**: the vertical position of the text.
- **text**: the content of the text.
- **fontSize**: the size of the font.
- **fontName**: the name of the font.
- **fontStyle**: bold, italic, or boldItalic
- **textAlign**: the alignment of the text like 'topLeft', 'middleCenter', 'bottomRight'.

Library
=======

Global
------

**print()** - `print('a')` => `a`

**range()** - `print(range(3))` => `[0, 1, 2]`

**newArray()** - `print(newArray(3, 2))` => `[[null, null], [null, null], [null, null]]`

**parseInt()** - `print(parseInt('5'))` => `5`

**parseDouble()** - `print(parseDouble('5.5'));` => `5.5`

Function
--------

**apply()** - `print.apply(null, [1]);` => `1`

**call()** - `print.call(null, 1, 2);` => `1 2`

String
------

**length()** - `print('12'.length());` => `2`

**charAt()** - `print('12'.charAt(1));` => `2`

**concat()** - `print('12'.concat('34'));` => `1234`

**endsWith()** - `print('aaa'.endsWith('a'));` => `true`

**indexOf()** - `print('aaab'.indexOf('b'));` => `3`

**lastIndexOf()** - `print('aaab'.lastIndexOf('a'));` => `2`

**repeat()** - `print('ab'.repeat(3));` => `ababab`

**substring()** - `print('abcd'.substring(1, 3));` => `bc`

**split()** - `print('a,b'.split(','));` => `[a, b]`

**startsWith()** - `print('abcd'.startsWith('ab'));` => `true`

**toLowerCase()** - `print('ABCD'.toLowerCase());` => `abcd`

**toUpperCase()** - `print('abcd'.toUpperCase());` => `ABCD`

**trim()** - `print(' ab cd '.trim());` => `ab cd`

Object
------

**keys()** - `print(({a:1, b:2}).keys());` => `[a, b]`

**values()** - `print(({a:1, b:2}).values());` => `[1, 2]`

**length()** - `print([1, 2, 3].length());` => `3`

**concat()** - `print([1, 2].concat([3], [4]));` => `[1, 2, 3, 4]`

**every()** - `print([1, 2, 3].every(function(x) { return x >= 1; }));` => `true`

**filter()** - `print([1, 2, 3, 4, 5].filter(function(x) { return x >= 3; }));` => `[3, 4, 5]`

**find()** - `print([1, 2, 3, 4, 5].find(function(x) { return x == 3; }));` => `3`

**findIndex()** - `print([1, 2, 3, 4, 5].findIndex(function(x) { return x == 3; }));` => `2`

**forEach()** - `[1, 2, 3].forEach(function(x) { print(x); });` => `1\n2\n3`

**indexOf()** - `print([1, 2, 3, 4, 5].indexOf(3));` => `2`

**join()** - `print([1, 2, 3, 4, 5].join(','));` => `1,2,3,4,5`

**lastIndexOf()** - `print([3, 3, 3, 3, 3].lastIndexOf(3));` => `4`

**map()** - `print([1, 2, 3, 4].map(function(x) { return x * 2; }));` => `[2, 4, 6, 8]`

**flatMap()** - `print([1, 2].flatMap(function(x) { return [x + 1, x + 2]; }));` => `[2, 3, 3, 4]`

**reduce()** - `print([1, 2, 3, 4].reduce(function(prev, x) { return prev + x; }, 0));` => `10`

**reduceRight()** - `print([1, 2, 3, 4].reduceRight(function(prev, x) { return prev + x; }, 0));` => `10`

**some()** - `print([1, 2, 3, 4].some(function(x) { return x == 1; }));` => `true`

**slice()** - `print([1, 2, 3, 4].slice(1, 3));` => `[2, 3]`

**sort()** - `var a = [10, 1, 2]; a.sort((x, y) => x - y); print(a);` => `[1, 2, 10]`

**pop()** - `var a = [1, 2, 3]; a.pop(); print(a);` => `[1, 2]`

**push()** - `var a = [1, 2, 3]; a.push(4); print(a);` => `[1, 2, 3, 4]`

**pushAll()** - `var a = [1, 2, 3]; a.pushAll([4, 5]); print(a);` => `[1, 2, 3, 4, 5]`

**reverse()** - `var a = [1, 2, 3]; a.reverse(); print(a);` => `[3, 2, 1]`

**shift()** - `var a = [1, 2, 3]; a.shift(); print(a);` => `[2, 3]`

**unshift()** - `var a = [1, 2, 3]; a.unshift(4); print(a);` => `[4, 1, 2, 3]`

**keys()** - `var a = [1, 2, 3, 4]; print(a.keys());` => `[0, 1, 2, 3]`

**values()** - `var a = [1, 2, 3, 4]; print(a.values());` => `[1, 2, 3, 4]`

Math
----

**E** - `print(Math.E);` => `2.718281828459045`

**LN2** - `print(Math.LN2);` => `0.6931471805599453`

**LN10** - `print(Math.LN10);` => `2.302585092994046`

**LOG2E** - `print(Math.LOG2E);` => `1.4426950408889634`

**LOG10E** - `print(Math.LOG10E);` => `0.4342944819032518`

**PI** - `print(Math.PI);` => `3.141592653589793`

**SQRT1_2** - `print(Math.SQRT1_2);` => `0.7071067811865476`

**SQRT2** - `print(Math.SQRT2);` => `1.4142135623730951`

**abs()** - `print(Math.abs(-2));` => `2`

**acos()** - `print(Math.acos(0.5));` => `1.0471975511965979`

**acosh()** - `print(Math.acosh(2));` => `1.3169578969248166`

**asin()** - `print(Math.asin(0.5));` => `0.5235987755982989`

**asinh()** - `print(Math.asinh(0.5));` => `0.48121182505960347`

**atan()** - `print(Math.atan(0.5));` => `0.4636476090008061`

**atanh()** - `print(Math.atanh(0.5));` => `0.5493061443340548`

**atan2()** - `print(Math.atan2(10, 20));` => `0.4636476090008061`

**cbrt()** - `print(Math.cbrt(100));` => `4.641588833612778`

**ceil()** - `print(Math.ceil(10.5));` => `11`

**cos()** - `print(Math.cos(0.5));` => `0.8775825618903728`

**cosh()** - `print(Math.cosh(0.5));` => `1.1276259652063807`

**exp()** - `print(Math.exp(1));` => `2.718281828459045`

**expm1()** - `print(Math.expm1(1));` => `1.718281828459045`

**floor()** - `print(Math.floor(10.5));` => `10`

**hypot()** - `print(Math.hypot(2, 3, 4));` => `5.385164807134504`

**log()** - `print(Math.log(100));` => `4.605170185988092`

**log1p()** - `print(Math.log1p(100));` => `4.61512051684126`

**log10()** - `print(Math.log10(100));` => `2`

**log2()** - `print(Math.log2(100));` => `6.643856189774724`

**max()** - `print(Math.max(2, 4, 8));` => `8`

**min()** - `print(Math.min(2, 4, 8));` => `2`

**pow()** - `print(Math.pow(2, 3));` => `8`

**random()** - `print(Math.random() < 1);` => `true`

**round()** - `print(Math.round(20.3));` => `20`

**sign()** - `print(Math.sign(-5));` => `-1`

**sin()** - `print(Math.sin(0.5));` => `0.479425538604203`

**sinh()** - `print(Math.sinh(0.5));` => `0.5210953054937474`

**sqrt()** - `print(Math.sqrt(2));` => `1.4142135623730951`

**tan()** - `print(Math.tan(0.5));` => `0.5463024898437905`

**tanh()** - `print(Math.tanh(0.5));` => `0.46211715726000974`

**trunc()** - `print(Math.trunc(3.5));` => `3`
