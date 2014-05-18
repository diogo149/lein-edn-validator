(ns leiningen.edn-validator
  (:require [instaparse.core :as insta]
            [clojure.edn :as edn]))

;; Converting edn into it's subforms

(def form-parser*
  (insta/parser
   "<S> = (form | neither)*
    form = paren-form | bracket-form | brace-form
    <paren-form> = '(' S ')'
    <bracket-form> = '[' S ']'
    <brace-form> = '{' S '}'
    <neither> = #'[^\\[\\]\\(\\)\\{\\}]'"))

(defn form-parser
  [s]
  (let [output (form-parser* s)]
    (if (insta/failure? output)
      (throw (Exception. (with-out-str (print output))))
      output)))

(defn form->nested
  "Assumes that all form children are already in the output form (as in a
   postwalk"
  [p]
  (cond
   (keyword? p) nil ;; shouldn't matter, since the only keyword is :form
   (string? p) {:string p :children nil :type :char}
   (vector? p) (form->nested (rest p))
   (seq? p) {:string (clojure.string/join (map :string p))
             :children p
             :type :form}))

(defn edn->nested
  [edn]
  (->> edn
       form-parser
       (clojure.walk/postwalk form->nested)))

;; Finding which forms have problems

(defn edn-error
  [s]
  (try (read-string s)
       nil
       (catch Exception e (.getMessage e))))

(defn add-error
  [f]
  (if (and (map? f) (= :form (:type f)))
    (assoc f :error (edn-error (:string f)))
    f))

(defn enrich-with-errors
  [nested]
  (clojure.walk/prewalk add-error nested))

;; Isolating the problematic forms


(defn isolate-errors
  [nested]
  (let [all-errors (atom []) ;; using at atom for a stateful walk
        walk-fn (fn [x]
                  (when (and (map? x) (:error x))
                    (let [{:keys [string error children]} x
                          children-errors (set (keep :error children))]
                      (when-not (contains? children-errors error)
                        (swap! all-errors conj {:string string :error error}))))
                  x)]
    (clojure.walk/prewalk walk-fn nested)
    @all-errors))

;; displaying

(defn pprint-errors
  [errors]
  (doseq [{:keys [string error]} errors]
    (println "Error occured in form: " string)
    (println "With error message:    " error)
    (println)))

;; all together

(defn validate-edn
  [edn]
  (->> edn
       edn->nested
       enrich-with-errors
       isolate-errors
       pprint-errors))

(defn ^:no-project-needed edn-validator
  [project & args]
  ;; Execute program with options
  (doseq [arg args]
    (validate-edn (slurp arg))))
