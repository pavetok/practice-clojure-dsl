(ns practice-clojure-dsl.practices
  (:require [datomic.api :as d]))

(def practice-schema
  [{:db/ident :val/eq
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/many}
   {:db/ident :eq/in
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/many}])

(comment
  (def practice-example
    {:practice/name "foo"}))
