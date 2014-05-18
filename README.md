# edn-validator

An edn validator that isolates errors to the smallest possible form. Useful for clojurescript errors.

## Usage

Put `[edn-validator "0.2.0-SNAPSHOT"]` into the `:plugins` vector of your project.clj.

## Example ##

    $ echo '{a}' > sample.clj
    $ lein edn-validator sample.clj
    Error occured in form:  {a}
    With error message:     Map literal must contain an even number of forms


    # if not at the root dir of the project
    $ lein edn-validator `pwd`/sample.cljs

## Known Issues ##

- False negative for `#js` (No reader function for tag js)

## License

Copyright © 2014 diogo149

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
