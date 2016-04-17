  - [3.4](#es6-computed-properties) Use computed property names when creating objects with dynamic property names.

    > Why? They allow you to define all the properties of an object in one place.

    ```javascript

    function getKey(k) {
      return `a key named ${k}`;
    }

    // bad
    const obj = {
      id: 5,
      name: 'San Francisco',
    };
    obj[getKey('enabled')] = true;

    // good
    const obj = {
      id: 5,
      name: 'San Francisco',
      [getKey('enabled')]: true,
    };
    ```