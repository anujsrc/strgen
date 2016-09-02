(ns miner.test-strgen
  (:require [clojure.test :refer [deftest is]]
            [clojure.test.check.generators :as gen]
            [clojure.spec :as s]
            [miner.strgen :as sg]))

(def ^:dynamic *exercise-limit* 5000)

(def regexes [#"f.o"
              #"f.*o+"
              #":k[a-z]o"
              #":k[a-z]/f\d*o+"
              #"s[a-z]o"
              #"s[a-z]/f\d*o+"
              #"(foo|bar|(baz+|quux?){2})+a?b+"
              #"((s[a-z]*)|\d+)(x[a-j]y|y[^-A-Za-z]z|pq|PQ)\w@[^A-Zaz]"
              ;; email example from spec guide
              #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$"
              #"\.\?\w\[\][-.?]"])



(defn test-re
  ([re] (test-re re *exercise-limit*))
  ([re limit]
   (every? (partial re-matches re)
           (gen/sample (sg/string-generator re) limit))))

(deftest gen-regexes
  (doseq [re regexes]
    (is (test-re re))))


(defn test-spec-re
  ([re] (test-re re *exercise-limit*))
  ([re limit]
   (every? #(apply = %)
           (s/exercise (s/spec (s/and string? #(re-matches re %))
                               :gen #(sg/string-generator re))
                       limit))))

(deftest spec-regexes
  (doseq [re regexes]
    (is (test-spec-re re))))


