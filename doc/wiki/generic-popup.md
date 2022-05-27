# Generic popup

## Context

At the time of creating this feature we already had
a [way to create tooltips/popovers](https://medium.com/magnetcoop/data-driven-tooltips-popovers-in-re-frame-de70d5412151).
And by tooltips/popovers I mean any visual component that appears on top of the base
layer of the webapp in order to give more info or allow extra controls to a user.

But many designs had popovers that had some things in common:
- they were always centered (both vertically and horizontally)
- they very often were invalidating any clicks happening outside them
- the base layer should ideally be hidden behind a semi-transparent scrim
- there never appears more than one of those elements

In other words what we need is a popup.

So we decided to implement a special tooltip container that would host any popup.

## Usage

### Spawning popup

To spawn a popup simply run
```clojurescript
(rf/dispatch [::tooltip/register {:id "generic-popup"
                                  :component :div}])
```

You should see a popup with an empty div inside.

You can further config this popup by modifying those config keys:
- `:component` - in real case you probably want to pass a symbol of a UI component. Like this:
    ```clojurescript
    (defn greet []
      [:div "Hello everyone!"])
      
    (rf/dispatch [::tooltip/register {:id "generic-popup"
                                      :component greet}])
    ```
- `:argv` - if you want to pass arguments to your component, then populate this vector.
    - example 1
        ```clojurescript
        (defn greet [mname]
          [:div (str "Hello " mname)])
          
        (rf/dispatch [::tooltip/register {:id "generic-popup"
                                          :component greet
                                          :argv ["HOP team"]}])
        ``` 
    - example 2
        ```clojurescript
        (rf/dispatch [::tooltip/register {:id "generic-popup"
                                          :component :div
                                          :argv [{:style {:background :tomato}}
                                                 "Hello!"]}])
        ```
- `:lightbox?` - Set to false if you don't want the semitransparent scrim outside of the popup
(defaults to `true`)
- `:modal?` - Set to `true` if you want any click outside of the popup to be ignored. (defaults to `false`)
    - WARNING - Be sure to implement other closing popup means (an 'x' button at least)
    
### Destroying popup
To close a popup simply run
```clojurescript
(rf/dispatch [::tooltip/destroy-by-id "generic-popup"])
```
