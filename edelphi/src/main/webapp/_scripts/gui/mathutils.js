Object.extend(Math, {
  getGCD : function(a, b) {
    // TODO: Replace this with own version.
    // Thanks for this snippet goes to to Nayuki Minase (http://nayuki.eigenstate.org/res/calculate-gcd-javascript.js)

    while (b != 0) {
      var z = a % b;
      a = b;
      b = z;
    }

    return a;
  }
});