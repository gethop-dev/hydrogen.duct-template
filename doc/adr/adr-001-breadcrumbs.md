# Add breadcrumbs feature to the template

## Status - proposed

## Context

In many projects we have a need to display [Location based](https://en.wikipedia.org/wiki/Breadcrumb_navigation#Types) breadcrumbs.
Each breadcrumb needs to be able to:
- display its name
- compose a redirect url

Important thing to note here is that there are two scenarios how a user can end up in a specific view:
1. The user starts from home page and incrementally moves towards the target location.
This way it's easy to build the breadcrumbs as the link the user clicks is probably going
to also be the name of the breadcrumb to add (provided that it's an incrementation and not
major reroute)
2. The user goes to a view directly from a bookmark/by refreshing. That way we need to ensure
that we know what data we want to query, query it and wait until the results are in.

Big part of this was exploring how flexible this feature should be.
And turns out that there are a lot of *it depends* factors.

### Factor 1 - urls structure
Let's assume we are building an ecommerce. That ecommerce has categories and items assigned to categories.
A url to a specific item could look somewhat like this:
```
/#/shop/category/fruits-111/category/citrus-222/item/lemon-999
```
This way we specify a full location path to an item. But if we assume that each category has exactly one parent
(except the root categories)
then we are free to leave out only one category:
```
/#/shop/category/citrus-222/item/lemon-999
```
We can squash it even more if we assume that each item has only one category (or we are able to reliably choose the *main one*):

```
/#/shop/item/lemon-999
```

So the first scenario is ideal for knowing what info to query from the backend. We simply destructure the url
for the three ids and query the backend for their respective data.

In any other case however we'll need a recursion of sorts that will be sure to know the whole path. That could be
either by the BE on its own (however that's not ideal as that way we couple FE navigational map with BE)
or we let BE return relevant info about that node and we can let FE decided if that the end of the loop.

But even if we are fine with long urls they still have other drawbacks. Imagine this very likely scenario:

In the home page of the shop we have categories listing in a sidebar and *all products* listing in the main screen.

That way, in order to build a redirect url for `lemon-999` product, we still need to query for its tree ancestors.

### Factor 2 - do node types repeat?

In the example above we saw that a shop item can be preceded by any number of categories. That means that when querying
for data we have to be prepared that the appdb will be populated with, probably, a collection of `category` maps.

But in other domains we might have a structure that is far simpler. Take a school registry as an example:
`school-111/grade-222/student-1337`. We know that the structure is always `school->grade->strudent`. That way our appdb
could be populated with something as simple as
```clojure
{:student {.. ..} :grade {.. ..} :school {.. ..}}
```

## Decision

Because of all the complexity this feature involves, the template will only be given:
- a namespace with a UI component for displaying breadcrumbs registered in the appdb
- re-frame subscription for getting breadcrumbs from the appdb
- re-frame event handler for populating appdb with breadcrumbs data.

The missing piece - where to get the breadcrumbs data **from** - will have to be individually applied given the specific project domain.
However I assume that using `re-frame-async-flow-fx` might be helpful:

```clojure
(defn- set-breadcrumbs-event-fx
  [{:keys [db]} [_ category-id id]]
  {:pre [(:category db)
         (:shop-item db)]}
  {:dispatch [::breadcrumbs/set
              [{:title "Shop"
                :url "/#/shop"}
               {:title (get-in db [:category :name])
                :url (str "/#/shop/" category-id)}
               {:title (get-in db [:shop-item :name])
                :url (str "/#/shop/" category-id "/" id)
                :disabled true}]]})

(rf/reg-event-fx ::set-breadcrumbs set-breadcrumbs-event-fx)

(rf/reg-event-fx
  ::fetch-breadcrumbs
  (fn [_ [_ category-id id]]
    {:dispatch-n [[::hydrogen-demo.category/get category-id]]
     :async-flow {:rules [{:when :seen-all-of?
                           :events [::got ::hydrogen-demo.category/got]
                           :dispatch [::set-breadcrumbs category-id id]}]}}))

(rf/reg-event-fx
  ::go-to-shop-item
  (fn [{:keys [db]} [_ category-id id]]
    {:dispatch-n [[::view/set-active-view :shop-item]
                  [::fetch-breadcrumbs category-id id]
                  [::get id]]
     :db (dissoc db :shop-item)
     :redirect (str "/#/shop/" id)}))
```
