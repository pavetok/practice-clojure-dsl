(ns practice-clojure-dsl.schema-test
  (:require [clojure.test :refer :all])
  (:require [practice-clojure-dsl.schema :refer :all]))

(def entities-with-primitives
  [{:data {}
    :schema []}
   {:data {:a 0}
    :schema [#:db{:ident :a
                  :valueType :db.type/long
                  :cardinality :db.cardinality/one}]}
   {:data {:a ""}
    :schema [#:db{:ident :a
                  :valueType :db.type/string
                  :cardinality :db.cardinality/one}]}
   {:data {:a true}
    :schema [#:db{:ident :a
                  :valueType :db.type/boolean
                  :cardinality :db.cardinality/one}]}])

(def entities-with-refs
  [{:data :a
    :schema [#:db{:ident :a}]}
   {:data {:a :b}
    :schema [#:db{:ident :b}
             #:db{:ident :a
                  :valueType :db.type/ref
                  :cardinality :db.cardinality/one}]}
   {:data {:a {:b 0}}
    :schema [#:db{:ident :a
                  :valueType :db.type/ref
                  :cardinality :db.cardinality/one}
             #:db{:ident :b
                  :valueType :db.type/long
                  :cardinality :db.cardinality/one}]}])

(def entities-with-collections
  [{:data {:a #{:b}}
    :schema [#:db{:ident :a
                  :valueType :db.type/ref
                  :cardinality :db.cardinality/many}
             #:db{:ident :b}]}
   {:data {:a #{:b :c}}
    :schema [#:db{:ident :a
                  :valueType :db.type/ref
                  :cardinality :db.cardinality/many}
             #:db{:ident :c}
             #:db{:ident :b}]}])

(deftest schemify-test
  (doseq [entity entities-with-primitives]
    (is (= (:schema entity) (schemify (:data entity) []))))
  (doseq [entity entities-with-refs]
    (is (= (:schema entity) (schemify (:data entity) []))))
  (doseq [entity entities-with-collections]
    (is (= (:schema entity) (schemify (:data entity) [])))))
