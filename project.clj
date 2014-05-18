(defproject edn-validator "0.2.0-SNAPSHOT"
  :description
  "An edn validator that isolates errors to the smallest possible form."
  :url "https://github.com/diogo149/lein-edn-validator"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[instaparse "1.3.2"]]
  :repl-options {:init-ns leiningen.edn-validator}
  ;; :pedantic? :warn
  :eval-in-leiningen true)
