(ns practice-clojure-dsl.constructs
  (:require [datomic.api :as d]))

(def construct-schema
  [{:db/ident :rel/eq
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/many}
   {:db/ident :eq/in
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/many}])
