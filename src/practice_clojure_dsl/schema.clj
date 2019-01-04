(ns practice-clojure-dsl.schema
  (:import (clojure.lang Keyword IPersistentMap IMapEntry PersistentArrayMap PersistentHashSet IPersistentSet)))

(defn- type-of
  [v]
  (get {Long :db.type/long
        String :db.type/string
        Boolean :db.type/boolean
        Keyword :db.type/ref
        IPersistentMap :db.type/ref
        PersistentArrayMap :db.type/ref
        PersistentHashSet :db.type/ref}
       (type v)))

(defn- cardinality-of
  [v]
  (get {PersistentHashSet :db.cardinality/many}
       (type v) :db.cardinality/one))

(defprotocol Schemify
  (schemify [data schema]))

(extend-protocol Schemify
  Keyword
  (schemify
    [kw schema]
    (conj schema #:db{:ident kw}))
  IMapEntry
  (schemify
    [[k v] schema]
    (conj
      (schemify v schema)
      #:db{:ident k
           :valueType (type-of v)
           :cardinality (cardinality-of v)}))
  IPersistentMap
  (schemify
    [data schema]
    (mapcat #(schemify % schema) data))
  IPersistentSet
  (schemify
    [data schema]
    (mapcat #(schemify % schema) data))
  Object
  (schemify
    [_ schema]
    schema))

(comment
  (schemify {} [])
  (schemify :a [])
  (schemify [:a 0] [])
  (schemify {:a 0} [])
  (schemify {:a :b} [])
  (schemify {:a #{:b}} []))
